package org.jetbrains.plugins.scheme.formatter.processors;

import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.scheme.formatter.SchemeBlock;
import org.jetbrains.plugins.scheme.parser.AST;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;

/**
 * @author ilyas
 */
public class SchemeIndentProcessor implements AST
{
  public static Indent getChildIndent(SchemeBlock parent, ASTNode prevChildNode, ASTNode child)
  {
    ASTNode astNode = parent.getNode();
    PsiElement psiParent = astNode.getPsi();

    // For Groovy file
    if (psiParent instanceof SchemeFile)
    {
      return Indent.getNoneIndent();
    }

    ASTNode node = parent.getNode();
    TokenSet L_BRACES = TokenSet.create(LEFT_CURLY, LEFT_PAREN, LEFT_SQUARE);
    if (LIST_LIKE_FORMS.contains(node.getElementType()))
    {
      if (L_BRACES.contains(child.getElementType()))
      {
        return Indent.getNoneIndent();
      }
      else
      {
        return Indent.getNormalIndent();
      }
    }
    return Indent.getNoneIndent();
  }
}
