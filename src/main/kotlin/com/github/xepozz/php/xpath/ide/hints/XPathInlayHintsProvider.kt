package com.github.xepozz.php.xpath.ide.hints

import com.github.xepozz.php.xpath.util.XPathUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.InlayProperties
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import java.awt.Graphics
import java.awt.Rectangle

/**
 * Utility class for showing XPath inline hints.
 */
object XPathInlayHintsProvider {
    private val activeHints = mutableMapOf<Int, MutableList<Inlay<*>>>()

    fun showHint(editor: Editor, element: PsiElement) {
        val xpath = XPathUtil.generateXPathForElement(element)
        val document = editor.document
        val lineNumber = document.getLineNumber(element.textRange.endOffset)
        val lineEndOffset = document.getLineEndOffset(lineNumber)

        // Remove all existing XPath hints from the file
        removeAllHintsFromFile(editor)

        // Add the new hint
        val inlayModel = editor.inlayModel
        val inlay = inlayModel.addInlineElement(
            lineEndOffset,
            InlayProperties()
                .priority(99)
                .showWhenFolded(true),
            XPathHintRenderer(xpath),
        )

        // Store the inlay reference for this line
        if (inlay != null) {
            activeHints.getOrPut(lineNumber) { mutableListOf() }.add(inlay)
        }
    }

    private fun removeAllHintsFromFile(editor: Editor) {
        // Clear all entries from the activeHints map and dispose of all inlays
        activeHints.forEach { (_, inlays) ->
            inlays.forEach { inlay ->
                inlay.dispose()
            }
        }
        activeHints.clear()

        // Parse all inlays in the entire file and remove any that are XPath inlays
        val inlayModel = editor.inlayModel
        val document = editor.document
        val startOffset = 0
        val endOffset = document.textLength

        inlayModel.getInlineElementsInRange(startOffset, endOffset).forEach { inlay ->
            if (inlay.renderer is XPathHintRenderer) {
                inlay.dispose()
            }
        }
    }

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
