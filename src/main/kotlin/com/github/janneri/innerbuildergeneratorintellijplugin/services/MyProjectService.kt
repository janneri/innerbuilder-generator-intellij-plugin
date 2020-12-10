package com.github.janneri.innerbuildergeneratorintellijplugin.services

import com.intellij.openapi.project.Project
import com.github.janneri.innerbuildergeneratorintellijplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
