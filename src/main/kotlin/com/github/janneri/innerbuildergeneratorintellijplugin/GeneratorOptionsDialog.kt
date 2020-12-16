package com.github.janneri.innerbuildergeneratorintellijplugin

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.uiDesigner.core.AbstractLayout
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JPanel

@Suppress("MagicNumber")
class GeneratorOptionsDialog : DialogWrapper(true) {
    private var generateCopyMethod: JBCheckBox = JBCheckBox("Generate Copy Method")
    private var withPrefix: JBTextField = JBTextField()

    init {
        init()
        title = "Generate Builder - Set Options"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gridbag = GridBag()
            .setDefaultWeightX(1.0)
            .setDefaultFill(GridBagConstraints.HORIZONTAL)
            .setDefaultInsets(Insets(0, 0, AbstractLayout.DEFAULT_VGAP, AbstractLayout.DEFAULT_HGAP))

        panel.preferredSize = Dimension(400, 50)

        panel.add(getLabel("Builder method prefix:"), gridbag.nextLine().next().weightx(0.2))
        panel.add(withPrefix, gridbag.next().weightx(0.8))

        panel.add(getLabel("Generate Copy Method?"), gridbag.nextLine().next().weightx(0.2))
        panel.add(generateCopyMethod, gridbag.next().weightx(0.8))

        return panel
    }

    fun getSelectedOptions(): GeneratorOptions {
        return GeneratorOptions(generateCopyMethod.isSelected, withPrefix.text)
    }

    private fun getLabel(text: String): JComponent {
        val label = JBLabel(text)
        label.componentStyle = UIUtil.ComponentStyle.SMALL
        label.fontColor = UIUtil.FontColor.BRIGHTER
        label.border = JBUI.Borders.empty(0, 5, 2, 0)
        return label
    }
}
