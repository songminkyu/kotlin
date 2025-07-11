/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.psiUtil

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.lexer.KtTokens.MODALITY_MODIFIERS
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub
import org.jetbrains.kotlin.types.expressions.OperatorConventions
import java.util.*

// NOTE: in this file we collect only Kotlin-specific methods working with PSI and not modifying it

// ----------- Calls and qualified expressions ---------------------------------------------------------------------------------------------

fun KtCallElement.getCallNameExpression(): KtSimpleNameExpression? {
    val calleeExpression = calleeExpression ?: return null

    return when (calleeExpression) {
        is KtSimpleNameExpression -> calleeExpression
        is KtConstructorCalleeExpression -> calleeExpression.constructorReferenceExpression
        else -> null
    }
}

/**
 * Returns enclosing qualifying element for given [[KtSimpleNameExpression]]
 * ([[KtQualifiedExpression]] or [[KtUserType]] or original expression)
 */
fun KtSimpleNameExpression.getQualifiedElement(): KtElement {
    val baseExpression = (parent as? KtCallExpression) ?: this
    val parent = baseExpression.parent
    return when (parent) {
        is KtQualifiedExpression -> if (parent.selectorExpression == baseExpression) parent else baseExpression
        is KtUserType -> if (parent.referenceExpression == baseExpression) parent else baseExpression
        else -> baseExpression
    }
}

fun KtSimpleNameExpression.getQualifiedElementOrCallableRef(): KtElement {
    val parent = parent
    if (parent is KtCallableReferenceExpression && parent.callableReference == this) return parent

    return getQualifiedElement()
}

fun KtSimpleNameExpression.getTopmostParentQualifiedExpressionForSelector(): KtQualifiedExpression? {
    return generateSequence<KtExpression>(this) {
        val parentQualified = it.parent as? KtQualifiedExpression
        if (parentQualified?.selectorExpression == it) parentQualified else null
    }.last() as? KtQualifiedExpression
}

/**
 * Returns rightmost selector of the qualified element (null if there is no such selector)
 */
fun KtElement.getQualifiedElementSelector(): KtElement? {
    return when (this) {
        is KtSimpleNameExpression -> this
        is KtCallExpression -> calleeExpression
        is KtQualifiedExpression -> {
            val selector = selectorExpression
            (selector as? KtCallExpression)?.calleeExpression ?: selector
        }
        is KtUserType -> referenceExpression
        else -> null
    }
}

fun KtSimpleNameExpression.getReceiverExpression(): KtExpression? {
    val parent = parent
    when {
        parent is KtQualifiedExpression -> {
            val receiverExpression = parent.receiverExpression
            // Name expression can't be receiver for itself
            if (receiverExpression != this) {
                return receiverExpression
            }
        }
        parent is KtCallExpression -> {
            //This is in case `a().b()`
            val grandParent = parent.parent
            if (grandParent is KtQualifiedExpression) {
                val parentsReceiver = grandParent.receiverExpression
                if (parentsReceiver != parent) {
                    return parentsReceiver
                }
            }
        }
        parent is KtBinaryExpression && parent.operationReference == this -> {
            return if (parent.operationToken in OperatorConventions.IN_OPERATIONS) parent.right else parent.left
        }
        parent is KtUnaryExpression && parent.operationReference == this -> {
            return parent.baseExpression
        }
        parent is KtUserType -> {
            val qualifier = parent.qualifier
            if (qualifier != null) {
                return qualifier.referenceExpression!!
            }
        }
    }

    return null
}

fun KtElement.getQualifiedExpressionForSelector(): KtQualifiedExpression? {
    val parent = parent
    return if (parent is KtQualifiedExpression && parent.selectorExpression == this) parent else null
}

fun KtExpression.getQualifiedExpressionForSelectorOrThis(): KtExpression {
    return getQualifiedExpressionForSelector() ?: this
}

fun KtExpression.getQualifiedExpressionForReceiver(): KtQualifiedExpression? {
    val parent = parent
    return if (parent is KtQualifiedExpression && parent.receiverExpression == this) parent else null
}

fun KtExpression.getQualifiedExpressionForReceiverOrThis(): KtExpression {
    return getQualifiedExpressionForReceiver() ?: this
}

fun KtExpression.isDotReceiver(): Boolean =
    (parent as? KtDotQualifiedExpression)?.receiverExpression == this

fun KtExpression.isDotSelector(): Boolean =
    (parent as? KtDotQualifiedExpression)?.selectorExpression == this

fun KtExpression.getPossiblyQualifiedCallExpression(): KtCallExpression? =
    ((this as? KtQualifiedExpression)?.selectorExpression ?: this) as? KtCallExpression

// ---------- Block expression -------------------------------------------------------------------------------------------------------------

fun KtElement.blockExpressionsOrSingle(): Sequence<KtElement> =
    if (this is KtBlockExpression) statements.asSequence() else sequenceOf(this)

fun KtExpression.lastBlockStatementOrThis(): KtExpression = (this as? KtBlockExpression)?.statements?.lastOrNull() ?: this

fun KtBlockExpression.contentRange(): PsiChildRange {
    val lBrace = this.lBrace ?: return PsiChildRange.EMPTY
    val rBrace = this.rBrace ?: return PsiChildRange.EMPTY

    val first = lBrace.siblings(withItself = false).firstOrNull { it !is PsiWhiteSpace }
    if (first == rBrace) return PsiChildRange.EMPTY

    val last = rBrace.siblings(forward = false, withItself = false).first { it !is PsiWhiteSpace }
    if (last == lBrace) return PsiChildRange.EMPTY

    return PsiChildRange(first, last)
}

// ----------- Inheritance -----------------------------------------------------------------------------------------------------------------

fun KtClass.isAbstract(): Boolean = isInterface() || hasModifier(KtTokens.ABSTRACT_KEYWORD)

/**
 * Returns the list of unqualified names that are indexed as the superclass names of this class. For the names that might be imported
 * via an aliased import, includes both the original and the aliased name (reference resolution during inheritor search will sort this out).
 *
 * @return the list of possible superclass names
 */
fun StubBasedPsiElementBase<out KotlinClassOrObjectStub<out KtClassOrObject>>.getSuperNames(): List<String> {
    fun addSuperName(result: MutableList<String>, referencedName: String) {
        result.add(referencedName)

        val file = containingFile
        if (file is KtFile) {
            getImportedSimpleNameByImportAlias(file, referencedName)?.let(result::add)
        }
    }

    require(this is KtClassOrObject) { "it should be ${KtClassOrObject::class} but it is a ${this::class.java.name}" }

    val stub = greenStub
    if (stub != null) {
        return stub.getSuperNames()
    }

    val specifiers = this.superTypeListEntries
    if (specifiers.isEmpty()) return Collections.emptyList()

    val result = ArrayList<String>()
    for (specifier in specifiers) {
        val superType = specifier.typeAsUserType
        if (superType != null) {
            val referencedName = superType.referencedName
            if (referencedName != null) {
                addSuperName(result, referencedName)
            }
        }
    }

    return result
}

// ------------ Annotations ----------------------------------------------------------------------------------------------------------------

// Annotations on labeled expression lies on it's base expression
fun KtExpression.getAnnotationEntries(): List<KtAnnotationEntry> {
    val parent = parent
    return when (parent) {
        is KtAnnotatedExpression -> parent.annotationEntries
        is KtLabeledExpression -> parent.getAnnotationEntries()
        else -> emptyList()
    }
}

fun KtAnnotationsContainer.collectAnnotationEntriesFromStubOrPsi(): List<KtAnnotationEntry> {
    return when (this) {
        is StubBasedPsiElementBase<*> -> stub?.collectAnnotationEntriesFromStubElement() ?: collectAnnotationEntriesFromPsi()
        else -> collectAnnotationEntriesFromPsi()
    }
}

private fun StubElement<*>.collectAnnotationEntriesFromStubElement(): List<KtAnnotationEntry> {
    return childrenStubs.flatMap { child ->
        when (child.stubType) {
            KtNodeTypes.ANNOTATION_ENTRY -> listOf(child.psi as KtAnnotationEntry)
            KtNodeTypes.ANNOTATION -> (child.psi as KtAnnotation).entries
            else -> emptyList()
        }
    }
}

private fun KtAnnotationsContainer.collectAnnotationEntriesFromPsi(): List<KtAnnotationEntry> {
    return children.flatMap { child ->
        when (child) {
            is KtAnnotationEntry -> listOf(child)
            is KtAnnotation -> child.entries
            else -> emptyList()
        }
    }
}

// -------- Recursive tree visiting --------------------------------------------------------------------------------------------------------

// Calls `block` on each descendant of T type
// Note, that calls happen in order of DFS-exit, so deeper nodes are applied earlier
inline fun <reified T : KtElement> forEachDescendantOfTypeVisitor(noinline block: (T) -> Unit): KtVisitorVoid {
    return object : KtTreeVisitorVoid() {
        override fun visitKtElement(element: KtElement) {
            super.visitKtElement(element)
            if (element is T) {
                block(element)
            }
        }
    }
}

inline fun <reified T : KtElement, R> flatMapDescendantsOfTypeVisitor(
    accumulator: MutableCollection<R>,
    noinline map: (T) -> Collection<R>,
): KtVisitorVoid {
    return forEachDescendantOfTypeVisitor<T> { accumulator.addAll(map(it)) }
}

// ----------- Contracts -------------------------------------------------------------------------------------------------------------------
/**
 * Whether the declaration may have a legacy contract (a contract that defined inside the body).
 *
 * In other words, **false** guarantees that the declaration cannot have a contract,
 * but **true** does not guarantee that the declaration has a contract.
 */
@KtImplementationDetail
fun KtDeclarationWithBody.isLegacyContractPresentPsiCheck(): Boolean {
    val statement = bodyBlockExpression?.firstStatement ?: return false
    val unwrappedExpression = statement.unwrapParenthesesLabelsAndAnnotations() as? KtExpression ?: return false
    return unwrappedExpression.isContractDescriptionCallPsiCheck()
}

fun KtNamedFunction.isContractPresentPsiCheck(isAllowedOnMembers: Boolean): Boolean {
    val contractAllowedHere =
        (isAllowedOnMembers || isTopLevel) &&
                hasBlockBody() &&
                !hasModifier(KtTokens.OPERATOR_KEYWORD)
    if (!contractAllowedHere) return false

    val firstExpression = (this as? KtFunction)?.bodyBlockExpression?.statements?.firstOrNull() ?: return false

    return firstExpression.isContractDescriptionCallPsiCheck()
}

fun KtExpression.isContractDescriptionCallPsiCheck(): Boolean =
    (this is KtCallExpression && calleeExpression?.text == "contract") || (this is KtQualifiedExpression && isContractDescriptionCallPsiCheck())

@OptIn(KtPsiInconsistencyHandling::class)
fun KtQualifiedExpression.isContractDescriptionCallPsiCheck(): Boolean {
    val expression = selectorExpression ?: return false
    return receiverExpressionOrNull?.text == "kotlin.contracts" && expression.isContractDescriptionCallPsiCheck()
}

fun KtElement.isFirstStatement(): Boolean {
    var parent = parent
    var element = this
    if (parent is KtDotQualifiedExpression) {
        element = parent
        parent = parent.parent
    }
    return parent is KtBlockExpression && parent.firstStatement == element
}


// ----------- Other -----------------------------------------------------------------------------------------------------------------------

fun KtClassOrObject.effectiveDeclarations(): List<KtDeclaration> {
    return when (this) {
        is KtClass -> getDeclarations() + getPrimaryConstructorParameters().filter { p -> p.hasValOrVar() }
        else -> declarations
    }
}

fun PsiElement.isExtensionDeclaration(): Boolean {
    val callable: KtCallableDeclaration? = when (this) {
        is KtNamedFunction, is KtProperty -> this as KtCallableDeclaration
        is KtPropertyAccessor -> getNonStrictParentOfType<KtProperty>()
        else -> null
    }

    return callable?.receiverTypeReference != null
}

fun KtDeclaration.isExpectDeclaration(): Boolean = when {
    hasExpectModifier() -> true
    this is KtParameter -> ownerDeclaration?.isExpectDeclaration() == true
    else -> containingClassOrObject?.isExpectDeclaration() == true
}

fun KtElement.isContextualDeclaration(): Boolean {
    val contextReceivers = when (this) {
        is KtCallableDeclaration -> contextReceivers
        is KtClassOrObject -> contextReceivers
        else -> emptyList()
    }
    return contextReceivers.isNotEmpty()
}

fun KtClassOrObject.isObjectLiteral(): Boolean = this is KtObjectDeclaration && isObjectLiteral()

//TODO: strange method, and not only Kotlin specific (also Java)
fun PsiElement.parameterIndex(): Int {
    val parent = parent
    return when (this) {
        is KtParameter if parent is KtParameterList -> parent.parameters.indexOf(this)
        is KtParameter if parent is KtContextReceiverList -> parent.contextParameters().indexOf(this)
        is PsiParameter if parent is PsiParameterList -> parent.getParameterIndex(this)
        else -> -1
    }
}

val KtValueArgument.argumentIndex: Int get() = (parent as KtValueArgumentList).arguments.indexOf(this)

fun KtModifierListOwner.isPrivate(): Boolean = hasModifier(KtTokens.PRIVATE_KEYWORD)

fun KtModifierListOwner.isProtected(): Boolean = hasModifier(KtTokens.PROTECTED_KEYWORD)

fun KtSimpleNameExpression.isImportDirectiveExpression(): Boolean {
    val parent = parent
    return parent is KtImportDirective || parent.parent is KtImportDirective
}

fun KtSimpleNameExpression.isPackageDirectiveExpression(): Boolean {
    val parent = parent
    return parent is KtPackageDirective || parent.parent is KtPackageDirective
}

fun KtExpression.isInImportDirective(): Boolean {
    return parents.takeWhile { it !is KtDeclaration && it !is KtBlockExpression }.any { it is KtImportDirective }
}

fun KtExpression.isLambdaOutsideParentheses(): Boolean {
    val parent = parent
    return when (parent) {
        is KtLambdaArgument -> true
        is KtLabeledExpression -> parent.isLambdaOutsideParentheses()
        else -> false
    }
}

fun KtExpression.getAssignmentByLHS(): KtBinaryExpression? {
    val parent = parent as? KtBinaryExpression ?: return null
    return if (KtPsiUtil.isAssignment(parent) && parent.left == this) parent else null
}

tailrec fun findAssignment(element: PsiElement?): KtBinaryExpression? =
    when (val parent = element?.parent) {
        is KtBinaryExpression -> if (parent.left == element && parent.operationToken == KtTokens.EQ) parent else null
        is KtQualifiedExpression -> findAssignment(element.parent)
        is KtSimpleNameExpression -> findAssignment(element.parent)
        else -> null
    }

fun KtStringTemplateExpression.getContentRange(): TextRange {
    val interpolationPrefixOrOpenQuote = node.firstChildNode ?: return TextRange.EMPTY_RANGE
    val openQuoteAfterPrefixOrNull = interpolationPrefixOrOpenQuote.treeNext?.takeIf { secondNode ->
        interpolationPrefixOrOpenQuote.elementType == KtNodeTypes.STRING_INTERPOLATION_PREFIX && secondNode.elementType == KtTokens.OPEN_QUOTE
    }
    val start = interpolationPrefixOrOpenQuote.textLength + (openQuoteAfterPrefixOrNull?.textLength ?: 0)
    val lastChild = node.lastChildNode
    val length = textLength
    return TextRange(start, if (lastChild.elementType == KtTokens.CLOSING_QUOTE) length - lastChild.textLength else length)
}

/**
 * Check expression might be a callee of call with the same name.
 * Note that 'this' in 'this(args)' isn't considered to be a callee, also 'name' is not a callee in 'name++'.
 */
fun KtSimpleNameExpression.isCallee(): Boolean {
    val parent = parent
    return when (parent) {
        is KtCallElement -> parent.calleeExpression == this
        is KtBinaryExpression -> parent.operationReference == this
        else -> {
            val callElement =
                getStrictParentOfType<KtUserType>()
                    ?.getStrictParentOfType<KtTypeReference>()
                    ?.getStrictParentOfType<KtConstructorCalleeExpression>()
                    ?.getStrictParentOfType<KtCallElement>()

            if (callElement != null) {
                val ktConstructorCalleeExpression = callElement.calleeExpression as? KtConstructorCalleeExpression
                (ktConstructorCalleeExpression?.typeReference?.typeElement as? KtUserType)?.referenceExpression == this
            } else {
                false
            }
        }
    }
}

val KtStringTemplateExpression.plainContent: String
    get() = getContentRange().substring(text)

fun KtStringTemplateExpression.isSingleQuoted(): Boolean =
    node.findChildByType(KtTokens.OPEN_QUOTE)?.textLength == 1

val KtNamedDeclaration.isPrivateNestedClassOrObject: Boolean get() = this is KtClassOrObject && isPrivate() && !isTopLevel()

fun KtNamedDeclaration.getValueParameters(): List<KtParameter> {
    return getValueParameterList()?.parameters ?: Collections.emptyList()
}

fun KtNamedDeclaration.getValueParameterList(): KtParameterList? {
    return when (this) {
        is KtCallableDeclaration -> valueParameterList
        is KtClass -> getPrimaryConstructorParameterList()
        else -> null
    }
}

fun KtExpression.asAssignment(): KtBinaryExpression? =
    if (KtPsiUtil.isAssignment(this)) this as KtBinaryExpression else null

private fun KtModifierList.modifierFromTokenSet(set: TokenSet): PsiElement? {
    return set.types
        .asSequence()
        .map { getModifier(it as KtModifierKeywordToken) }
        .firstOrNull { it != null }

}

private fun KtModifierListOwner.modifierFromTokenSet(set: TokenSet) = modifierList?.modifierFromTokenSet(set)

fun KtModifierList.visibilityModifier() = modifierFromTokenSet(KtTokens.VISIBILITY_MODIFIERS)

fun KtModifierList.visibilityModifierType(): KtModifierKeywordToken? = visibilityModifier()?.node?.elementType as KtModifierKeywordToken?

fun KtModifierListOwner.visibilityModifier() = modifierList?.modifierFromTokenSet(KtTokens.VISIBILITY_MODIFIERS)

val KtModifierListOwner.isPublic: Boolean
    get() {
        if (this is KtDeclaration && KtPsiUtil.isLocal(this)) return false
        val visibilityModifier = visibilityModifierType()
        return visibilityModifier == null || visibilityModifier == KtTokens.PUBLIC_KEYWORD
    }

fun KtModifierListOwner.visibilityModifierType(): KtModifierKeywordToken? =
    visibilityModifier()?.node?.elementType as KtModifierKeywordToken?

fun KtModifierListOwner.visibilityModifierTypeOrDefault(): KtModifierKeywordToken =
    visibilityModifierType() ?: KtTokens.DEFAULT_VISIBILITY_KEYWORD

fun KtDeclaration.modalityModifier() = modifierFromTokenSet(MODALITY_MODIFIERS)

fun KtDeclaration.modalityModifierType(): KtModifierKeywordToken? = modalityModifier()?.node?.elementType as KtModifierKeywordToken?

fun KtStringTemplateExpression.isPlain() = entries.all { it is KtLiteralStringTemplateEntry }
fun KtStringTemplateExpression.isPlainWithEscapes() =
    entries.all { it is KtLiteralStringTemplateEntry || it is KtEscapeStringTemplateEntry }

// Correct for class members only (including constructors and nested classes)
// Returns null e.g. for member function parameters, member function locals, property accessors
val KtDeclaration.containingClassOrObject: KtClassOrObject?
    get() = parent.let {
        when (it) {
            is KtClassBody -> it.parent as? KtClassOrObject
            is KtClassOrObject -> it
            is KtParameterList -> (it.parent as? KtPrimaryConstructor)?.getContainingClassOrObject()
            else -> null
        }
    }

fun KtExpression.getOutermostParenthesizerOrThis(): KtExpression {
    return (parentsWithSelf.zip(parents)).firstOrNull {
        val (element, parent) = it
        when (parent) {
            is KtParenthesizedExpression -> false
            is KtAnnotatedExpression -> parent.baseExpression != element
            is KtLabeledExpression -> parent.baseExpression != element
            else -> true
        }
    }?.first as KtExpression? ?: this
}

fun PsiElement.isFunctionalExpression(): Boolean = this is KtNamedFunction && nameIdentifier == null

private val BAD_NEIGHBOUR_FOR_SIMPLE_TEMPLATE_ENTRY_PATTERN = Regex("([a-zA-Z0-9_]|[^\\p{ASCII}]).*")

fun canPlaceAfterSimpleNameEntry(element: PsiElement?): Boolean {
    val entryText = element?.text ?: return true
    return !BAD_NEIGHBOUR_FOR_SIMPLE_TEMPLATE_ENTRY_PATTERN.matches(entryText)
}

fun KtElement.nonStaticOuterClasses(): Sequence<KtClass> {
    return generateSequence(containingClass()) { if (it.isInner()) it.containingClass() else null }
}

fun KtElement.containingClass(): KtClass? = getStrictParentOfType()

fun KtClassOrObject.findPropertyByName(name: String): KtNamedDeclaration? {
    return declarations.firstOrNull { it is KtProperty && it.name == name } as KtNamedDeclaration?
        ?: primaryConstructorParameters.firstOrNull { it.hasValOrVar() && it.name == name }
}

fun KtClassOrObject.findFunctionByName(name: String): KtNamedDeclaration? {
    return declarations.firstOrNull { it is KtNamedFunction && it.name == name } as KtNamedDeclaration?
}

fun isTypeConstructorReference(e: PsiElement): Boolean {
    val parent = e.parent
    return parent is KtUserType && parent.referenceExpression == e
}

fun KtParameter.isPropertyParameter() = ownerFunction is KtPrimaryConstructor && hasValOrVar()

fun isDoubleColonReceiver(expression: KtExpression) =
    expression.getParentOfTypeAndBranch<KtDoubleColonExpression> { this.receiverExpression } != null

fun KtFunctionLiteral.getOrCreateParameterList(): KtParameterList {
    valueParameterList?.let { return it }

    val psiFactory = KtPsiFactory(project)

    val anchor = lBrace
    val newParameterList = addAfter(psiFactory.createLambdaParameterList("x"), anchor) as KtParameterList
    newParameterList.removeParameter(0)
    if (arrow == null) {
        val whitespaceAndArrow = psiFactory.createWhitespaceAndArrow()
        addRangeAfter(whitespaceAndArrow.first, whitespaceAndArrow.second, newParameterList)
    }
    return newParameterList
}

fun KtFunctionLiteral.findLabelAndCall(): Pair<Name?, KtCallExpression?> {
    val literalParent = (this.parent as KtLambdaExpression).parent

    fun KtValueArgument.callExpression(): KtCallExpression? {
        val parent = parent
        return (if (parent is KtValueArgumentList) parent else this).parent as? KtCallExpression
    }

    when (literalParent) {
        is KtLabeledExpression -> {
            val callExpression = (literalParent.parent as? KtValueArgument)?.callExpression()
            return Pair(literalParent.getLabelNameAsName(), callExpression)
        }

        is KtValueArgument -> {
            val callExpression = literalParent.callExpression()
            val label = (callExpression?.calleeExpression as? KtSimpleNameExpression)?.getReferencedNameAsName()
            return Pair(label, callExpression)
        }

        else -> {
            return Pair(null, null)
        }
    }
}

fun KtCallExpression.getOrCreateValueArgumentList(): KtValueArgumentList {
    valueArgumentList?.let { return it }
    return addAfter(
        KtPsiFactory(project).createCallArguments("()"),
        typeArgumentList ?: calleeExpression,
    ) as KtValueArgumentList
}

fun KtCallExpression.addTypeArgument(typeArgument: KtTypeProjection) {
    if (typeArgumentList != null) {
        typeArgumentList?.addArgument(typeArgument)
    } else {
        addAfter(KtPsiFactory(project).createTypeArguments("<${typeArgument.text}>"), calleeExpression)
    }
}

fun KtDeclaration.hasBody() = when (this) {
    is KtFunction -> hasBody()
    is KtProperty -> hasBody()
    else -> false
}


fun KtExpression.referenceExpression(): KtReferenceExpression? =
    (if (this is KtCallExpression) calleeExpression else this) as? KtReferenceExpression

fun KtExpression.getLabeledParent(labelName: String): KtLabeledExpression? {
    parents.forEach {
        when (it) {
            is KtLabeledExpression -> if (it.getLabelName() == labelName) return it
            is KtParenthesizedExpression, is KtAnnotatedExpression, is KtLambdaExpression -> return@forEach
            else -> return null
        }
    }
    return null
}

fun PsiElement.astReplace(newElement: PsiElement) = parent.node.replaceChild(node, newElement.node)

var KtElement.parentSubstitute: PsiElement? by UserDataProperty(Key.create<PsiElement>("PARENT_SUBSTITUTE"))

private val HARD_KEYWORDS: Set<String> by lazy(LazyThreadSafetyMode.PUBLICATION) {
    KtTokens.KEYWORDS.types.mapTo(HashSet()) { (it as KtKeywordToken).value }
}

fun String?.isIdentifier(): Boolean {
    if (this == null || isEmpty()) return false

    if (startsWith("`")) {
        // Mirrors escaped identifier rules in the lexer (see Kotlin.flex)
        val unescaped = removeSurrounding("`")
        return unescaped.isNotEmpty() && unescaped.all { it != '`' && it != '\n' }
    }

    if (this in HARD_KEYWORDS) {
        return false
    }

    val length = this.length
    var index = 0

    while (index < length) {
        val codePoint = codePointAt(index)

        val isValid = if (codePoint < 256) {
            (codePoint == '_'.code)
                    || (codePoint in 'A'.code..'Z'.code)
                    || (codePoint in 'a'.code..'z'.code)
                    || (index > 0 && codePoint in '0'.code..'9'.code)
        } else {
            Character.isLetter(codePoint) || (index > 0 && Character.isDigit(codePoint))
        }

        if (!isValid) {
            return false
        }

        index += Character.charCount(codePoint)
    }

    return true
}

fun String.quoteIfNeeded(): String = if (this.isIdentifier()) this else "`$this`"

fun PsiElement.isTopLevelKtOrJavaMember(): Boolean {
    return when (this) {
        is KtDeclaration -> isKtFile(parent)
        is PsiClass -> containingClass == null && this.qualifiedName != null
        else -> false
    }
}

fun KtNamedDeclaration.safeNameForLazyResolve(): Name {
    return nameAsName.safeNameForLazyResolve()
}

fun Name?.safeNameForLazyResolve(): Name = this?.takeUnless(Name::isSpecial) ?: SpecialNames.NO_NAME_PROVIDED

fun KtNamedDeclaration.safeFqNameForLazyResolve(): FqName? {
    //NOTE: should only create special names for package level declarations, so we can safely rely on real fq name for parent
    val parentFqName = KtNamedDeclarationUtil.getParentFqName(this)
    return parentFqName?.child(safeNameForLazyResolve())
}

fun isTopLevelInFileOrScript(element: PsiElement): Boolean {
    val parent = element.parent
    return when (parent) {
        is KtFile -> true
        is KtBlockExpression -> parent.parent is KtScript
        else -> false
    }
}

fun KtFile.getFileOrScriptDeclarations() = if (isScript()) script!!.declarations else declarations

fun KtExpression.getBinaryWithTypeParent(): KtBinaryExpressionWithTypeRHS? {
    val callExpression = parent as? KtCallExpression ?: return null
    val possibleQualifiedExpression = callExpression.parent

    val targetExpression = if (possibleQualifiedExpression is KtQualifiedExpression) {
        if (possibleQualifiedExpression.selectorExpression != callExpression) return null
        possibleQualifiedExpression
    } else {
        callExpression
    }

    return targetExpression.topParenthesizedParentOrMe().parent as? KtBinaryExpressionWithTypeRHS
}

fun KtExpression.topParenthesizedParentOrMe(): KtExpression {
    var result: KtExpression = this
    while (KtPsiUtil.deparenthesizeOnce(result.parent as? KtExpression) == result) {
        result = result.parent as? KtExpression ?: break
    }
    return result
}

fun getTrailingCommaByClosingElement(closingElement: PsiElement?): PsiElement? {
    val elementBeforeClosingElement =
        closingElement?.getPrevSiblingIgnoringWhitespaceAndComments() ?: return null

    return elementBeforeClosingElement.run { if (node.elementType == KtTokens.COMMA) this else null }
}

fun getTrailingCommaByElementsList(elementList: PsiElement?): PsiElement? {
    val lastChild = elementList?.lastChild?.let { if (it !is PsiComment) it else it.getPrevSiblingIgnoringWhitespaceAndComments() }
    return lastChild?.takeIf { it.node.elementType == KtTokens.COMMA }
}

val KtNameReferenceExpression.isUnderscoreInBackticks
    get() = getReferencedName() == "`_`"

tailrec fun KtTypeElement.unwrapNullability(): KtTypeElement? {
    return when (this) {
        is KtNullableType -> this.innerType?.unwrapNullability()
        else -> this
    }
}

internal fun isKtFile(parent: PsiElement?): Boolean {
    //avoid loading KtFile which depends on java psi, which is not available in some setup
    //e.g. remote dev https://youtrack.jetbrains.com/issue/GTW-7554
    return parent is PsiFile && parent.language == KotlinLanguage.INSTANCE
}

fun getImportedSimpleNameByImportAlias(file: KtFile, aliasName: String): String? {
    val directive = file.findImportByAlias(aliasName) ?: return null

    var reference = directive.importedReference
    while (reference is KtDotQualifiedExpression) {
        reference = reference.selectorExpression
    }
    if (reference is KtSimpleNameExpression) {
        return reference.getReferencedName()
    }

    return null
}

/**
 * A best-effort way to get the class id of expression's type without resolve.
 */
fun KtConstantExpression.inferClassIdByPsi(): ClassId? =
    ClassIdCalculator.inferConstantExpressionClassIdByPsi(this)
