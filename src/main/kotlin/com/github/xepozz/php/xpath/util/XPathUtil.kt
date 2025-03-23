package com.github.xepozz.php.xpath.util

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement

object XPathUtil {
    fun generateXPathForElement(element: PsiElement): String {
        try {
            // This is a simple implementation. In a real plugin, you would generate
            // an actual XPath expression based on the element's position in the document.
            val xpath = "//" + element.node.elementType.toString()
            return xpath
        } catch (e: Exception) {
            return "//error"
        }
    }

    fun showXPathAlert(psiElement: PsiElement) {
        try {
            val xpath = generateXPathForElement(psiElement)

            Messages.showInfoMessage(
                "XPath for the selected element:\n$xpath",
                "XPath Information"
            )
        } catch (e: Exception) {
            throw e
        }
    }
}
