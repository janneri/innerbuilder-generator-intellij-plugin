package com.github.janneri.innerbuildergeneratorintellijplugin

data class GeneratorOptions(
    val generateCopyMethod: Boolean,
    val methodPrefix: String,
    val paramName: String,
    val jsonDeserializeWithBuilder: Boolean,
)
