/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlinx.metadata.klib

import kotlinx.metadata.klib.impl.klibExtensions
import kotlin.metadata.*
import kotlin.metadata.internal.common.KmModuleFragment

var KmFunction.uniqId: UniqId?
    get() = klibExtensions.uniqId
    set(value) {
        klibExtensions.uniqId = value
    }

var KmFunction.file: KlibSourceFile?
    get() = klibExtensions.file
    set(value) {
        klibExtensions.file = value
    }

var KmClass.uniqId: UniqId?
    get() = klibExtensions.uniqId
    set(value) {
        klibExtensions.uniqId = value
    }

var KmClass.file: KlibSourceFile?
    get() = klibExtensions.file
    set(value) {
        klibExtensions.file = value
    }

var KmProperty.uniqId: UniqId?
    get() = klibExtensions.uniqId
    set(value) {
        klibExtensions.uniqId = value
    }

var KmProperty.file: KlibSourceFile?
    get() = klibExtensions.file
    set(value) {
        klibExtensions.file = value
    }

var KmProperty.compileTimeValue: KmAnnotationArgument?
    get() = klibExtensions.compileTimeValue
    set(value) {
        klibExtensions.compileTimeValue = value
    }

val KmType.annotations: MutableList<KmAnnotation>
    get() = klibExtensions.annotations

var KmConstructor.uniqId: UniqId?
    get() = klibExtensions.uniqId
    set(value) {
        klibExtensions.uniqId = value
    }

var KmPackage.fqName: String?
    get() = klibExtensions.fqName
    set(value) {
        klibExtensions.fqName = value
    }

var KmModuleFragment.fqName: String?
    get() = klibExtensions.fqName
    set(value) {
        klibExtensions.fqName = value
    }

val KmModuleFragment.className: MutableList<ClassName>
    get() = klibExtensions.className

val KmModuleFragment.moduleFragmentFiles: MutableList<KlibSourceFile>
    get() = klibExtensions.moduleFragmentFiles

val KmTypeParameter.annotations: MutableList<KmAnnotation>
    get() = klibExtensions.annotations

var KmTypeParameter.uniqId: UniqId?
    get() = klibExtensions.uniqId
    set(value) {
        klibExtensions.uniqId = value
    }

var KmTypeAlias.uniqId: UniqId?
    get() = klibExtensions.uniqId
    set(value) {
        klibExtensions.uniqId = value
    }

var KmEnumEntry.ordinal: Int?
    get() = klibExtensions.ordinal
    set(value) {
        klibExtensions.ordinal = value
    }

var KmEnumEntry.uniqId: UniqId?
    get() = klibExtensions.uniqId
    set(value) {
        klibExtensions.uniqId = value
    }
