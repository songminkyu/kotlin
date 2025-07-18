/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.low.level.api.fir.services.FirRenderingOptions
import org.jetbrains.kotlin.analysis.low.level.api.fir.services.firRenderingOptions
import org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.LLFirSessionCache
import org.jetbrains.kotlin.analysis.low.level.api.fir.test.configurators.AnalysisApiFirLibraryBinaryDecompiledTestConfigurator
import org.jetbrains.kotlin.analysis.low.level.api.fir.util.FirDeclarationForCompiledElementSearcher
import org.jetbrains.kotlin.analysis.test.framework.base.AbstractAnalysisApiBasedTest
import org.jetbrains.kotlin.analysis.test.framework.projectStructure.KtTestModule
import org.jetbrains.kotlin.analysis.test.framework.services.libraries.CompiledLibraryProvider
import org.jetbrains.kotlin.analysis.test.framework.services.libraries.TestModuleDecompiler
import org.jetbrains.kotlin.analysis.test.framework.services.libraries.TestModuleDecompilerDirectory
import org.jetbrains.kotlin.platform.CommonPlatforms
import org.jetbrains.kotlin.platform.js.JsPlatforms
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.model.SimpleDirectivesContainer
import org.jetbrains.kotlin.test.services.*

abstract class AbstractLibraryGetOrBuildFirTest : AbstractAnalysisApiBasedTest() {
    override val configurator get() = AnalysisApiFirLibraryBinaryDecompiledTestConfigurator

    override fun configureTest(builder: TestConfigurationBuilder) {
        val renderingOptionsBuilder = FirRenderingOptions.Builder().apply { renderKtText = true }

        builder.forTestsMatching("*/js/*") {
            this.defaultsProviderBuilder.targetPlatform = JsPlatforms.defaultJsPlatform
        }
        builder.forTestsMatching("*/jvm/*") {
            this.defaultsProviderBuilder.targetPlatform = JvmPlatforms.defaultJvmPlatform
        }
        builder.forTestsMatching("*/metadata/*") {
            this.defaultsProviderBuilder.targetPlatform = CommonPlatforms.defaultCommonPlatform
            this.useAdditionalService<TestModuleDecompiler> { TestModuleDecompilerDirectory() }
        }
        builder.forTestsMatching("*/containerSource/*") {
            renderingOptionsBuilder.renderKtFileName = true
            renderingOptionsBuilder.renderContainerSource = true
        }
        super.configureTest(builder)
        with(builder) {
            useDirectives(Directives)
            useAdditionalServices(
                service(::CompiledLibraryProvider),
                service<FirRenderingOptions> { _ -> renderingOptionsBuilder.build() }
            )
        }
    }

    override fun doTestByMainFile(mainFile: KtFile, mainModule: KtTestModule, testServices: TestServices) {
        val declaration = getElementToSearch(mainFile, testServices.moduleStructure)!!

        val ktModule = mainModule.ktModule
        val session = LLFirSessionCache.getInstance(mainModule.ktModule.project).getSession(ktModule, preferBinary = true)
        val fir = FirDeclarationForCompiledElementSearcher(session).findNonLocalDeclaration(declaration)

        testServices.assertions.assertEqualsToTestOutputFile(renderActualFir(fir, declaration, testServices.firRenderingOptions))
    }

    private fun getElementToSearch(ktFile: KtFile, moduleStructure: TestModuleStructure): KtDeclaration? {
        val expectedType = moduleStructure.allDirectives[Directives.DECLARATION_TYPE].firstOrNull()
            ?: error("Compiled code should have element type specified")
        @Suppress("UNCHECKED_CAST") val expectedClass = Class.forName(expectedType) as Class<out PsiElement>
        return findFirstDeclaration(ktFile.declarations, expectedClass)
    }

    private fun findFirstDeclaration(
        declarations: List<KtDeclaration>,
        expectedClass: Class<out PsiElement>,
    ): KtDeclaration? {
        declarations.filterIsInstance(expectedClass).firstOrNull()?.let { return it as KtDeclaration }
        declarations.forEach { decl ->
            if (decl is KtDeclarationContainer) {
                findFirstDeclaration(decl.declarations, expectedClass)?.let { return it }
            }
            if (decl is KtFunction) {
                findFirstDeclaration(decl.valueParameters, expectedClass)?.let { return it }
            }
            if (decl is KtProperty) {
                findFirstDeclaration(decl.accessors, expectedClass)?.let { return it }
            }
            if (decl is KtTypeParameterListOwner) {
                findFirstDeclaration(decl.typeParameters, expectedClass)?.let { return it }
            }
            if (decl is KtClass && KtConstructor::class.java.isAssignableFrom(expectedClass)) {
                decl.primaryConstructor?.let { return it }
            }
        }
        return null
    }

    private object Directives : SimpleDirectivesContainer() {
        val DECLARATION_TYPE by stringDirective("DECLARATION_TYPE")
    }
}
