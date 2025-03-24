package com.github.xepozz.php.xpath.listeners

import com.github.xepozz.php.xpath.ide.hints.XPathInlayHints
import com.github.xepozz.php.xpath.util.XPathUtil
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.psi.PsiDocumentManager

class EditorMouseClickListener : EditorMouseListener {
    override fun mouseClicked(event: EditorMouseEvent) {
        println("event: $event")
        if (event.mouseEvent.clickCount != 1) {
            return
        }

        val editor = event.editor
        val project = editor.project ?: return
        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

        val offset = editor.logicalPositionToOffset(editor.xyToLogicalPosition(event.mouseEvent.point))
        val psiElement = psiFile.findElementAt(offset) ?: return

        // Show both the alert and the inline hint
        XPathUtil.showXPathAlert(psiElement)
        XPathInlayHints.showHint(editor, psiElement)
    }
}
