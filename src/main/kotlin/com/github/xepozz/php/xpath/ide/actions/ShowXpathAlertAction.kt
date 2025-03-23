package com.github.xepozz.php.xpath.ide.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement

class ShowXpathAlertAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return
        val psiElement = psiFile.findElementAt(editor.caretModel.offset) ?: return

        val xpath = generateXPathForElement(psiElement)

        Messages.showInfoMessage(
            "XPath for the selected element:\n$xpath",
            "XPath Information"
        )
    }

    private fun generateXPathForElement(element: PsiElement): String {
        // This is a simple implementation. In a real plugin, you would generate
        // an actual XPath expression based on the element's position in the document.
        return "//" + element.node.elementType.toString()
    }

    override fun update(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return

        // Enable the action if we have an editor and a file
        event.presentation.isEnabledAndVisible = true
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}