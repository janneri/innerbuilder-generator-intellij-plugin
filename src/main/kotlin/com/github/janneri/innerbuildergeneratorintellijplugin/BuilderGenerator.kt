package com.github.janneri.innerbuildergeneratorintellijplugin

import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.addOrReplaceMethod
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.deleteConstructor
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.hasField
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.isList
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.isOptional
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.makeFirstLetterUpperCase
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import org.jetbrains.annotations.NotNull

object BuilderGenerator {
    private const val BUILDER_CLASS_NAME = "Builder"
    private const val NEW_BUILDER_FACTORY_METHOD_NAME = "builder"

    fun generateBuilder(dtoClass: PsiClass, builderMethodPrefix: String?, createCopyConstructor: Boolean = false) {
        val elementFactory = JavaPsiFacade.getElementFactory(dtoClass.project)
        val dtoFields = dtoClass.fields.filterNot { it.hasModifierProperty(PsiModifier.STATIC) }

        var builderClass: PsiClass? = dtoClass.findInnerClassByName(BUILDER_CLASS_NAME, false)
        val builderClassNotFound = builderClass == null

        if (builderClassNotFound) {
            // Create a static class named Builder
            val newBuilderClass = createBuilderClassWithPrivateConstructor(elementFactory)

            // Add the build-method to the Builder class
            newBuilderClass.add(createBuildMethod(dtoClass, newBuilderClass, elementFactory))

            // Add a static (factory) method for creating a new instance of the builder
            dtoClass.add(createNewBuilderMethod(elementFactory, dtoClass, newBuilderClass))

            builderClass = newBuilderClass
        }

        // create fields and setter methods to the builder if they don't exist
        addBuilderFieldsAndMethods(builderMethodPrefix, builderClass!!, dtoFields, elementFactory)

        removeExtraFieldsFromBuilder(builderClass, dtoFields, builderMethodPrefix)

        // recreate the copy constructor
        if (createCopyConstructor) {
            addOrReplaceMethod(dtoClass, createCopyMethod(dtoClass, dtoFields, elementFactory))
        }

        // recreate the constructor from builder instance to dto class instance
        deleteConstructor(dtoClass, "private", 1)
        dtoClass.add(createPrivateConstructorFromBuilder(dtoClass, dtoFields, elementFactory))

        if (builderClassNotFound) {
            dtoClass.add(builderClass)
        }
    }

    private fun createNewBuilderMethod(
        elementFactory: PsiElementFactory,
        dtoClass: PsiClass,
        newBuilderClass: PsiClass
    ): @NotNull PsiMethod {
        val newBuilderStaticMethod =
            elementFactory.createMethod(NEW_BUILDER_FACTORY_METHOD_NAME, elementFactory.createType(newBuilderClass))

        newBuilderStaticMethod.modifierList.setModifierProperty(PsiModifier.STATIC, true)
        newBuilderStaticMethod.body!!.add(
            elementFactory.createStatementFromText(
                """
                return new $BUILDER_CLASS_NAME();
                
                """.trimIndent(),
                dtoClass
            )
        )
        return newBuilderStaticMethod
    }

    private fun createBuilderClassWithPrivateConstructor(elementFactory: PsiElementFactory): PsiClass {
        val builderClass = elementFactory.createClass(BUILDER_CLASS_NAME)
        builderClass.modifierList!!.add(elementFactory.createKeyword("static"))

        val constructor = elementFactory.createConstructor()
        constructor.modifierList.setModifierProperty(PsiModifier.PUBLIC, false)
        constructor.modifierList.setModifierProperty(PsiModifier.PRIVATE, true)
        builderClass.add(constructor)

        return builderClass
    }

    private fun removeExtraFieldsFromBuilder(
        builderClass: PsiClass,
        dtoFields: List<PsiField>,
        builderMethodPrefix: String?
    ) {
        builderClass.fields.forEach { field ->
            val dtoField = dtoFields.find { it.name == field.name && it.type == field.type }
            if (dtoField == null) {
                field.delete()
                val methodName = methodName(builderMethodPrefix, field)
                builderClass.findMethodsByName(methodName, false).firstOrNull()?.delete()
            }
        }
    }

    private fun addBuilderFieldsAndMethods(
        builderMethodPrefix: String?,
        builderClass: PsiClass,
        psiFields: List<PsiField>,
        elementFactory: PsiElementFactory
    ) {
        val builderMethodReturnType = elementFactory.createType(builderClass)

        for (psiField in psiFields) {
            val field = elementFactory.createField(psiField.name, psiField.type)

            if (!hasField(builderClass, field)) {
                when {
                    isOptional(field.type) -> {
                        builderClass.add(
                            elementFactory.createFieldFromText(
                                field.text.replace(";", " = Optional.empty();"),
                                builderClass
                            )
                        )
                    }
                    isList(field.type) -> {
                        builderClass.add(
                            elementFactory.createFieldFromText(
                                field.text.replace(";", " = Collections.emptyList();"),
                                builderClass
                            )
                        )
                    }
                    else -> {
                        builderClass.add(field)
                    }
                }
            }

            val method = elementFactory.createMethod(methodName(builderMethodPrefix, field), builderMethodReturnType)
            val parameter = elementFactory.createParameter(psiField.name, psiField.type)
            method.parameterList.add(parameter)

            method.body!!.add(
                elementFactory.createStatementFromText(
                    "this.${psiField.name} = ${psiField.name};\n",
                    builderClass
                )
            )
            method.body!!.add(
                elementFactory.createStatementFromText("return this;\n", builderClass)
            )

            addOrReplaceMethod(
                builderClass,
                method,
                builderClass.findMethodsByName("build", false).firstOrNull()
            )
        }
    }

    private fun createBuildMethod(
        psiClass: PsiClass,
        builderClass: PsiClass,
        elementFactory: PsiElementFactory
    ): @NotNull PsiMethod {
        val buildMethod = elementFactory.createMethod("build", elementFactory.createType(psiClass))
        buildMethod.body!!.add(
            elementFactory.createStatementFromText(
                "return new " + elementFactory.createType(psiClass).name + "(this);",
                builderClass
            )
        )
        return buildMethod
    }

    private fun createPrivateConstructorFromBuilder(
        targetClass: PsiClass,
        fields: List<PsiField>,
        elementFactory: PsiElementFactory
    ): PsiMethod {
        var method = "private $BUILDER_CLASS_NAME(Builder builder) {\n"

        for (field in fields) {
            method += "${field.name} = builder.${field.name};\n"
        }

        method += "}\n"
        return elementFactory.createMethodFromText(method, targetClass)
    }

    private fun createCopyMethod(
        targetClass: PsiClass,
        fields: List<PsiField>,
        elementFactory: PsiElementFactory
    ): PsiMethod {
        var method = "public static $BUILDER_CLASS_NAME copy(${elementFactory.createType(targetClass).name} src) {\n"
        method += "Builder builder = new Builder();"

        for (field in fields) {
            method += "builder.${field.name} = src.${field.name};\n"
        }

        method += "return builder;\n"
        method += "}\n"
        return elementFactory.createMethodFromText(method, targetClass)
    }

    private fun methodName(builderMethodPrefix: String?, field: PsiField): String {
        return if (builderMethodPrefix.isNullOrEmpty()) field.name
        else builderMethodPrefix + makeFirstLetterUpperCase(field.name)
    }
}
