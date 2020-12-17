package com.github.janneri.innerbuildergeneratorintellijplugin

import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.getPsiClass
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.loadPersistedGeneratorOptions
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction

class GenerateAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiClass = getPsiClass(e) ?: return

        WriteCommandAction.runWriteCommandAction(psiClass.project) {
            val generator = BuilderGenerator(psiClass, loadPersistedGeneratorOptions())
            generator.generateBuilder()
        }
    }
}
