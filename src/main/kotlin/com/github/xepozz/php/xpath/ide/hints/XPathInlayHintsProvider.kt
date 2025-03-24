package com.github.xepozz.php.xpath.ide.hints

import com.github.xepozz.php.xpath.util.XPathUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import java.awt.Graphics
import java.awt.Rectangle

/**
 * Utility class for showing XPath inline hints.
 */
object XPathInlayHints {
    private val XPATH_HINT_KEY = Key<String>("xpath.hint.key")

    /**
     * Shows an XPath hint for the given element at the end of the line.
     */
    fun showHint(editor: Editor, element: PsiElement) {
        val xpath = XPathUtil.generateXPathForElement(element)
        val document = editor.document
        val lineNumber = document.getLineNumber(element.textRange.endOffset)
        val lineEndOffset = document.getLineEndOffset(lineNumber)

        // Remove any existing hints for this line
        removeHintsForLine(editor, lineNumber)

        // Add the new hint
        val inlayModel = editor.inlayModel
        inlayModel.addInlineElement(
            lineEndOffset,
            true,
            XPathHintRenderer("XPath: $xpath")
        )
    }

    /**
     * Removes all XPath hints for the given line.
     */
    private fun removeHintsForLine(editor: Editor, lineNumber: Int) {
        val document = editor.document
        val startOffset = document.getLineStartOffset(lineNumber)
        val endOffset = document.getLineEndOffset(lineNumber)

        val inlayModel = editor.inlayModel
        inlayModel.getInlineElementsInRange(startOffset, endOffset).forEach { inlay ->
            if (inlay.renderer is XPathHintRenderer) {
                inlay.dispose()
            }
        }
    }

    /**
     * Custom renderer for XPath hints.
     */
    private class XPathHintRenderer(private val text: String) : EditorCustomElementRenderer {
        override fun calcWidthInPixels(inlay: Inlay<*>): Int {
            return text.length * 7 // Approximate width based on text length
        }

        override fun paint(inlay: Inlay<*>, g: Graphics, r: Rectangle, textAttributes: TextAttributes) {
            g.color = JBColor.GRAY
            g.drawString(text, r.x, r.y + r.height - 2)
        }
    }
}
