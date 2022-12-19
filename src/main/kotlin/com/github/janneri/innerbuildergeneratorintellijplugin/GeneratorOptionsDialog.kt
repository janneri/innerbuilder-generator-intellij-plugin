package com.github.janneri.innerbuildergeneratorintellijplugin

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.uiDesigner.core.AbstractLayout
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.JBUI
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


@Suppress("MagicNumber")
class GeneratorOptionsDialog(persistedOptions: GeneratorOptions) : DialogWrapper(true) {
    private var generateCopyMethod: JBCheckBox = JBCheckBox(null, persistedOptions.generateCopyMethod)
    private var jsonDeserializeWithBuilder: JBCheckBox = JBCheckBox(null, persistedOptions.jsonDeserializeWithBuilder)
    private var methodPrefix: JBTextField = JBTextField(persistedOptions.methodPrefix)
    private var paramName: JBTextField = JBTextField(persistedOptions.paramName)

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
        panel.add(getLabel("Builder method param name:"), gridbag.weightx(0.2))
        gridbag.gridx += 1
        panel.add(paramName, gridbag.weightx(0.8))

        gridbag.gridx = 0
        gridbag.gridy += 1
        panel.add(getLabel("Generate a copy Method?"), gridbag.weightx(0.2))
        gridbag.gridx += 1
        panel.add(generateCopyMethod, gridbag.weightx(0.8))

        gridbag.gridx = 0
        gridbag.gridy += 1
        panel.add(getLabel("JsonDeserialize with builder?"), gridbag.weightx(0.2))
        gridbag.gridx += 1
        panel.add(jsonDeserializeWithBuilder, gridbag.weightx(0.8))

        gridbag.insetTop(40)
        gridbag.gridwidth = 2
        gridbag.gridx = 0
        gridbag.gridy += 1
        panel.add(JBLabel("Code sample:"), gridbag)

        gridbag.insetTop(-1)
        gridbag.gridy += 1
        val codeSample = JBTextArea(SampleCodeGenerator.generateSample(getSelectedOptions()))
        codeSample.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        panel.add(codeSample, gridbag)

        // Listen for changes in the text
        val textListener = SimpleDocumentListener { updateCodeSample(codeSample) }
        methodPrefix.document.addDocumentListener(textListener)
        paramName.document.addDocumentListener(textListener)

        // Listen for changes in the checkbox
        val listener = ActionListener { _: ActionEvent? -> updateCodeSample(codeSample) }
        generateCopyMethod.addActionListener(listener)
        jsonDeserializeWithBuilder.addActionListener(listener)

        return panel
    }

    private class SimpleDocumentListener(val callback: () -> Unit): DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) {
            callback()
        }

        override fun removeUpdate(e: DocumentEvent?) {
            callback()
        }

        override fun changedUpdate(e: DocumentEvent?) {
            callback()
        }
    }

    fun updateCodeSample(codeSampleLabel: JBTextArea) {
        codeSampleLabel.setText(SampleCodeGenerator.generateSample(getSelectedOptions()))
    }

    fun getSelectedOptions(): GeneratorOptions {
        return GeneratorOptions(generateCopyMethod.isSelected, methodPrefix.text, paramName.text,
            jsonDeserializeWithBuilder.isSelected)
    }

    private fun getLabel(text: String): JComponent {
        val label = JBLabel(text)
        label.border = JBUI.Borders.empty(0, 5, 2, 0)
        return label
    }
}
