package com.github.janneri.innerbuildergeneratorintellijplugin

import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.getPsiClass
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.loadPersistedGeneratorOptions
import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.persistGeneratorOptions
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction

class GenerateWithOptionsAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiClass = getPsiClass(e) ?: return

        val generatorOptionsDialog = GeneratorOptionsDialog(loadPersistedGeneratorOptions())
        val okPressed = generatorOptionsDialog.showAndGet()

        if (!okPressed) {
            return
        }

        val generatorOptions = generatorOptionsDialog.getSelectedOptions()

        // Persist the options to make them survive IDE restart.
        // Also to be able to use the previously selected settings,
        // when generating builders without opening the settings dialog again.
        persistGeneratorOptions(generatorOptions)

        WriteCommandAction.runWriteCommandAction(psiClass.project) {
            val generator = BuilderGenerator(psiClass, generatorOptions)
            generator.generateBuilder()
        }
    }
}
