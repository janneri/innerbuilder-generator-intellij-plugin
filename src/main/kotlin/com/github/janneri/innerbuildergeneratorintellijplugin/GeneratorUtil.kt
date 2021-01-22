
package com.github.janneri.innerbuildergeneratorintellijplugin

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiTreeUtil

object GeneratorUtil {

    fun getPsiClass(e: AnActionEvent): PsiClass? {
        val psiFile = e.getData(LangDataKeys.PSI_FILE)
        val editor: Editor? = e.getData(PlatformDataKeys.EDITOR)
        if (psiFile == null || editor == null) {
            return null
        }
        val offset: Int = editor.caretModel.offset
        val element = psiFile.findElementAt(offset)
        return PsiTreeUtil.getParentOfType(element, PsiClass::class.java)
    }

    fun addOrReplaceMethod(
        target: PsiClass,
        newMethod: PsiMethod,
        afterMethod: PsiMethod? = null,
        beforeMethod: PsiMethod? = null
    ): PsiMethod {
        val existingMethod: PsiMethod? = target.findMethodBySignature(newMethod, false)
        if (existingMethod != null) {
            existingMethod.replace(newMethod)
        } else {
            if (beforeMethod != null && afterMethod == null) {
                target.addBefore(newMethod, beforeMethod)
            } else if (afterMethod != null) {
                target.addAfter(newMethod, afterMethod)
            } else {
                target.add(newMethod)
            }
        }
        return newMethod
    }

    fun deleteConstructor(fromClass: PsiClass, withModifier: String, parameterCount: Int) {
        fromClass.constructors
            .firstOrNull { it.hasModifierProperty(withModifier) && it.parameterList.parametersCount == parameterCount }
            ?.delete()
    }

    fun hasField(clazz: PsiClass?, field: PsiField): Boolean {
        return clazz!!.findFieldByName(field.name, false) != null
    }

    fun firstLetterUpperCase(str: String): String {
        return str.substring(0, 1).toUpperCase() + str.substring(1)
    }

    fun isOptional(type: PsiType): Boolean {
        return type.toString().startsWith("PsiType:Optional<")
    }

    fun isList(type: PsiType): Boolean {
        return type.toString().startsWith("PsiType:List<")
    }

    // All plugins share the same namespace, so let's use a distinct prefix.
    private const val PROPERTY_PREFIX = "com.github.janneri.innerbuildergeneratorintellijplugin"

    fun persistGeneratorOptions(options: GeneratorOptions) {
        val propertiesComponent = PropertiesComponent.getInstance()
        propertiesComponent.setValue(PROPERTY_PREFIX + ".generateCopyMethod", options.generateCopyMethod, false)
        propertiesComponent.setValue(PROPERTY_PREFIX + ".methodPrefix", options.methodPrefix, "")
    }

    fun loadPersistedGeneratorOptions(): GeneratorOptions {
        val propertiesComponent = PropertiesComponent.getInstance()
        return GeneratorOptions(
            propertiesComponent.getBoolean(PROPERTY_PREFIX + ".generateCopyMethod", false),
            propertiesComponent.getValue(PROPERTY_PREFIX + ".methodPrefix", "")
        )
    }
}
