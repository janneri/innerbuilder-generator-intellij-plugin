package com.github.janneri.innerbuildergeneratorintellijplugin

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.uiDesigner.core.AbstractLayout
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JPanel

@Suppress("MagicNumber")
class GeneratorOptionsDialog(persistedOptions: GeneratorOptions) : DialogWrapper(true) {
    private var generateCopyMethod: JBCheckBox = JBCheckBox(null, persistedOptions.generateCopyMethod)
    private var methodPrefix: JBTextField = JBTextField(persistedOptions.methodPrefix)

    init {
        init()
        title = "Generate Builder With Settings"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gridbag = GridBag()
            .setDefaultWeightX(1.0)
            .setDefaultFill(GridBagConstraints.HORIZONTAL)
            .setDefaultInsets(Insets(0, 0, AbstractLayout.DEFAULT_VGAP, AbstractLayout.DEFAULT_HGAP))

        gridbag.gridx = 0
        gridbag.gridy = 0
        gridbag.gridwidth = 2
        gridbag.anchor(GridBagConstraints.FIRST_LINE_START)
        panel.add(getLabel("Note! These settings are saved and survive IDE restarts."), gridbag)
        gridbag.gridy += 1
        gridbag.insetBottom(20)
        panel.add(
            getLabel("To quickly generate a builder with the previously selected settings, just hit \"shift alt B\"."),
            gridbag
        )

        gridbag.gridwidth = 1
        gridbag.insetBottom(1)
        gridbag.anchor(GridBagConstraints.LINE_START)

        gridbag.gridx = 0
        gridbag.gridy += 1
        panel.add(getLabel("Builder method prefix:"), gridbag.weightx(0.2))
        gridbag.gridx += 1
        panel.add(methodPrefix, gridbag.weightx(0.8))

        gridbag.gridx = 0
        gridbag.gridy += 1
        panel.add(getLabel("Generate Copy Method?"), gridbag.weightx(0.2))
        gridbag.gridx += 1
        panel.add(generateCopyMethod, gridbag.weightx(0.8))

        return panel
    }

    fun getSelectedOptions(): GeneratorOptions {
        return GeneratorOptions(generateCopyMethod.isSelected, methodPrefix.text)
    }

    private fun getLabel(text: String): JComponent {
        val label = JBLabel(text)
        label.componentStyle = UIUtil.ComponentStyle.SMALL
        label.fontColor = UIUtil.FontColor.BRIGHTER
        label.border = JBUI.Borders.empty(0, 5, 2, 0)
        return label
    }
}
