package com.github.janneri.innerbuildergeneratorintellijplugin

import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.getPsiClass
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction

class GenerateWithOptionsAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiClass = getPsiClass(e) ?: return

        val generatorOptions = GeneratorOptions()
        val okPressed = generatorOptions.showAndGet()

        if (!okPressed) {
            return
        }

        WriteCommandAction.runWriteCommandAction(psiClass.project) {
            BuilderGenerator.generateBuilder(
                psiClass,
                generatorOptions.withPrefix(),
                generatorOptions.generateCopyMethod()
            )
        }
    }
}
