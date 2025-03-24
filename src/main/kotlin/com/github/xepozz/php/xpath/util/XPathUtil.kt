package com.github.xepozz.php.xpath.util

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IFileElementType
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import java.util.*

object XPathUtil {
    fun generateXPathForElement(element: PsiElement): String {
        try {
            val xpathParts = LinkedList<String>()
            var currentElement: PsiElement? = element

            while (currentElement != null) {
                val node = currentElement.node
                if (node == null) {
                    // Skip elements with null nodes
                    currentElement = currentElement.parent
                    continue
                }
                val elementName = getElementName(currentElement)
                val position = getElementPosition(currentElement)


//                \Xepozz\PhpXpathDemo\Kernel::invoke()::$var
//               /namespace[@name='\Xepozz\PhpXpathDemo']
//               /class[@name='\Xepozz\PhpXpathDemo\Kernel']
//               /method[@name='__invoke']
//               /variable[@name='var']
                if (currentElement is LeafPsiElement) {
                    println("skip $currentElement")
                    currentElement = currentElement.parent
                    continue
                }
                val xpathPart = when (node.elementType) {
                    PhpElementTypes.CLASS_METHOD -> "method[@name='$elementName']$position"
                    PhpElementTypes.CLASS_CONST -> {
                        val currentElement = currentElement as Field
                        "constant[@name='${currentElement.name}']$position"
                    }

                    PhpElementTypes.CLASS_FIELD -> {
                        val currentElement = currentElement as Field
                        "property[@name='${currentElement.name}']$position"
                    }

                    PhpElementTypes.CLASS -> {
                        val currentElement = currentElement as PhpClass
                        "class[@name='${currentElement.name}']$position"
                    }

                    PhpElementTypes.NAMESPACE -> {
                        val currentElement = currentElement as PhpNamedElement
                        "namespace[@name='${currentElement.fqn}']$position"
                    }

                    PhpElementTypes.VARIABLE -> {
                        val currentElement = currentElement as com.jetbrains.php.lang.psi.elements.Variable
                        "variable[@name='${currentElement.name}']$position"
                    }

                    is IFileElementType,
                    PhpElementTypes.STATEMENT,
                    PhpElementTypes.ASSIGNMENT_EXPRESSION,
                    PhpElementTypes.CLASS_CONSTANTS,
                    PhpElementTypes.CLASS_FIELDS,
                    PhpElementTypes.CLASS_REFERENCES_GROUP,
                    PhpElementTypes.GROUP_STATEMENT,
                    PhpElementTypes.NON_LAZY_GROUP_STATEMENT
                        -> {
                        currentElement = currentElement.parent
                        continue
                    }

                    else -> "${node} | ${node.elementType} | $position ==="
                }

                xpathParts.addFirst(xpathPart)
                currentElement = currentElement.parent
            }

            return "/" + xpathParts.joinToString("/")
        } catch (e: Exception) {
            Logger.getInstance(XPathUtil::class.java).error("Error generating XPath", e)
            return "//error"
        }
    }

    private fun getElementName(element: PsiElement): String {
        return if (element is PhpNamedElement) element.name else element.node.toString()
    }

    private fun getElementPosition(element: PsiElement): String {
        if (element is PhpNamedElement) return ""
        val parent = element.parent ?: return ""
        val node = element.node ?: return ""
        val elementType = node.elementType

        var position = 1
        var sibling: PsiElement? = element.prevSibling

        while (sibling != null) {
            val siblingNode = sibling.node
            if (siblingNode != null && siblingNode.elementType == elementType) {
                position++
            }
            sibling = sibling.prevSibling
        }

        return "[$position]"
    }

    fun showXPathAlert(psiElement: PsiElement) {
        val xpath = generateXPathForElement(psiElement)
        val project = psiElement.project

        NotificationGroupManager.getInstance()
            .getNotificationGroup("XPath Notifications")
            .createNotification(
                "XPath Information",
                "XPath for the selected element:\n$xpath",
                NotificationType.INFORMATION
            )
            .notify(project)
    }
}
