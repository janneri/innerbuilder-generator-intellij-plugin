package com.github.janneri.innerbuildergeneratorintellijplugin

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil


object GeneratorUtil {

    fun getPsiClass(e: AnActionEvent): PsiClass? {
        val psiFile = e.getData(LangDataKeys.PSI_FILE)
        val editor: Editor? = e.getData(PlatformDataKeys.EDITOR)
        if (psiFile == null || editor == null) {
            return null
        }
        val offset: Int = editor.getCaretModel().getOffset()
        val element = psiFile.findElementAt(offset)
        return PsiTreeUtil.getParentOfType(element, PsiClass::class.java)
    }

    fun addOrReplaceMethod(target: PsiClass, newMethod: PsiMethod, beforeMethod: PsiMethod? = null): PsiMethod {
        val existingMethod: PsiMethod? = target.findMethodBySignature(newMethod, false)
        if (existingMethod != null) {
            existingMethod.replace(newMethod)
        } else {
            if (beforeMethod != null) {
                target.addBefore(newMethod, beforeMethod)
            }
            else {
                target.add(newMethod)
            }
        }
        return newMethod
    }

    fun deleteConstructor(fromClass: PsiClass, withModifier: String, parameterCount: Int) {
        fromClass.constructors
            .firstOrNull { it.hasModifierProperty(withModifier) && it.parameters.size == parameterCount }
            ?.delete()
    }

    fun hasField(clazz: PsiClass?, field: PsiField): Boolean {
        return clazz!!.findFieldByName(field.getName(), false) != null
    }

    fun makeFirstLetterUpperCase(str: String): String {
        return str.substring(0, 1).toUpperCase() + str.substring(1)
    }

    fun isOptional(type: PsiType): Boolean {
        return type.toString().startsWith("PsiType:Optional<")
    }

    fun isList(type: PsiType): Boolean {
        return type.toString().startsWith("PsiType:List<")
    }

}