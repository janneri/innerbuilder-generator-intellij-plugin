package com.github.janneri.innerbuildergeneratorintellijplugin

import com.github.janneri.innerbuildergeneratorintellijplugin.GeneratorUtil.getPsiClass
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction

class GenerateAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiClass = getPsiClass(e) ?: return

        WriteCommandAction.runWriteCommandAction(psiClass.project) {
            val generator = BuilderGenerator(
                psiClass,
                GeneratorOptions(
                    System.getenv("BUILDER_PLUGIN_DO_NOT_CREATE_COPY_CONSTRUCTOR") != null,
                    System.getenv("BUILDER_PLUGIN_METHOD_PREFIX")
                )
            )
            generator.generateBuilder()
        }
    }
}
