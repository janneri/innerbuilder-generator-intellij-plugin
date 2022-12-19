package com.github.janneri.innerbuildergeneratorintellijplugin

import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.addAnnotation
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.addOrReplaceMethod
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.convertFirstLetterToUpperCase
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.deleteConstructor
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.hasAnnotation
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.hasField
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.isList
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.isOptional
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import org.jetbrains.annotations.NotNull

const val BUILDER_CLASS_NAME = "Builder"
const val NEW_BUILDER_FACTORY_METHOD_NAME = "builder"

const val DESERIALIZE_ANNOTATION_NAME = "JsonDeserialize"
const val JSONPOJO_BUILDER_ANNOTATION_NAME = "JsonPOJOBuilder"

class BuilderGenerator(private val dtoClass: PsiClass, private val options: GeneratorOptions) {
    private val elementFactory = JavaPsiFacade.getElementFactory(dtoClass.project)

    fun generateBuilder() {
        val dtoFields = dtoClass.fields.filterNot { it.hasModifierProperty(PsiModifier.STATIC) }

        var builderClass: PsiClass? = dtoClass.findInnerClassByName(BUILDER_CLASS_NAME, false)
        val builderClassNotFound = builderClass == null

        if (builderClassNotFound) {
            // Create a static class named Builder
            val newBuilderClass = createBuilderClassWithPrivateConstructor()

            // Add the build-method to the Builder class
            newBuilderClass.add(createBuildMethod(newBuilderClass))

            // Add a static (factory) method for creating a new instance of the builder
            dtoClass.add(createNewBuilderMethod(newBuilderClass))

            builderClass = newBuilderClass
        }

        // create fields and setter methods to the builder if they don't exist
        addBuilderFieldsAndMethods(builderClass!!, dtoFields)

        removeExtraFieldsFromBuilder(builderClass, dtoFields)

        // recreate the copy constructor
        if (options.generateCopyMethod) {
            addOrReplaceMethod(dtoClass, createCopyMethod(dtoFields))
        }

        // recreate the constructor from builder instance to dto class instance
        deleteConstructor(dtoClass, "private", 1)
        dtoClass.add(createPrivateConstructorFromBuilder(dtoFields, dtoClass.isRecord))

        if (options.jsonDeserializeWithBuilder) {
            // ehkä remove annotation olisi parempi
            if (!hasAnnotation(dtoClass, DESERIALIZE_ANNOTATION_NAME)) {
                val annotationAsText = "@$DESERIALIZE_ANNOTATION_NAME(builder = ${dtoClass.name}.Builder.class)"
                addAnnotation(dtoClass, annotationAsText, elementFactory)
            }

            if (!hasAnnotation(builderClass, JSONPOJO_BUILDER_ANNOTATION_NAME)) {
                val annotationAsText = "@$JSONPOJO_BUILDER_ANNOTATION_NAME(withPrefix = \"${options.methodPrefix}\")"
                addAnnotation(builderClass, annotationAsText, elementFactory)
            }
        }

        if (builderClassNotFound) {
            dtoClass.add(builderClass)
        }
    }

    private fun createNewBuilderMethod(newBuilderClass: PsiClass): @NotNull PsiMethod {
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

    private fun createBuilderClassWithPrivateConstructor(): PsiClass {
        val builderClass = elementFactory.createClass(BUILDER_CLASS_NAME)
        builderClass.modifierList!!.add(elementFactory.createKeyword("static"))

        val constructor = elementFactory.createConstructor()
        constructor.modifierList.setModifierProperty(PsiModifier.PUBLIC, false)
        constructor.modifierList.setModifierProperty(PsiModifier.PRIVATE, true)
        builderClass.add(constructor)

        return builderClass
    }

    private fun removeExtraFieldsFromBuilder(builderClass: PsiClass, dtoFields: List<PsiField>) {
        builderClass.fields.forEach { field ->
            val dtoField = dtoFields.find { it.name == field.name && it.type == field.type }
            if (dtoField == null) {
                field.delete()
                val methodName = methodName(options.methodPrefix, field)
                builderClass.findMethodsByName(methodName, false).firstOrNull()?.delete()
            }
        }
    }

    private fun addBuilderFieldsAndMethods(builderClass: PsiClass, psiFields: List<PsiField>) {
        val builderMethodReturnType = elementFactory.createType(builderClass)
        var prevMethod: PsiMethod? = null

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

            val method = elementFactory.createMethod(methodName(options.methodPrefix, field), builderMethodReturnType)
            val parameter = elementFactory.createParameter(paramName(options.paramName, psiField), psiField.type)
            method.parameterList.add(parameter)

            method.body!!.add(
                elementFactory.createStatementFromText(
                    "this.${psiField.name} = ${paramName(options.paramName, psiField)};\n",
                    builderClass
                )
            )
            method.body!!.add(
                elementFactory.createStatementFromText("return this;\n", builderClass)
            )

            // try to preserve order of methods
            addOrReplaceMethod(
                target = builderClass,
                newMethod = method,
                beforeMethod = builderClass.findMethodsByName("build", false).firstOrNull(),
                afterMethod = prevMethod
            )

            // prevMethod is either the new method or the existing method
            prevMethod = builderClass.findMethodBySignature(method, false)
        }
    }

    private fun createBuildMethod(builderClass: PsiClass): @NotNull PsiMethod {
        val buildMethod = elementFactory.createMethod("build", elementFactory.createType(dtoClass))
        buildMethod.body!!.add(
            elementFactory.createStatementFromText(
                "return new " + elementFactory.createType(dtoClass).name + "(this);",
                builderClass
            )
        )
        return buildMethod
    }

    private fun createPrivateConstructorFromBuilder(fields: List<PsiField>, isRecord: Boolean): PsiMethod {
        var method = "private $BUILDER_CLASS_NAME(Builder builder) {\n"

        if (isRecord) {
            // With records we need to call the default constructor with this(...)
            method += "this("
            method += fields.map{"builder." + it.name}.joinToString(",")
            method += ");"
        }
        else {
            for (field in fields) {
                method += "${field.name} = builder.${field.name};\n"
            }
        }

        method += "}\n"
        return elementFactory.createMethodFromText(method, dtoClass)
    }

    private fun createCopyMethod(fields: List<PsiField>): PsiMethod {
        var method = "public static $BUILDER_CLASS_NAME copy(${elementFactory.createType(dtoClass).name} src) {\n"
        method += "Builder builder = new Builder();"

        for (field in fields) {
            method += "builder.${field.name} = src.${field.name};\n"
        }

        method += "return builder;\n"
        method += "}\n"
        return elementFactory.createMethodFromText(method, dtoClass)
    }

    private fun methodName(builderMethodPrefix: String?, field: PsiField): String {
        return if (builderMethodPrefix.isNullOrEmpty()) field.name
        else builderMethodPrefix + convertFirstLetterToUpperCase(field.name)
    }

    private fun paramName(builderParamName: String?, field: PsiField): String {
        return if (builderParamName.isNullOrEmpty()) field.name else builderParamName
    }
}
