package org.jetbrains.plugins.scheme.editor.selection;

import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.editor.Editor;

import java.util.List;

import org.jetbrains.plugins.scheme.psi.api.ClLiteral;
import org.jetbrains.plugins.scheme.lexer.Tokens;

/**
 * @author ilyas
 */
public class SchemeLiteralSelectioner extends SchemeBasicSelectioner
{
  public boolean canSelect(PsiElement e)
  {
    PsiElement parent = e.getParent();
    return isStringLiteral(e) || isStringLiteral(parent);
  }

  private static boolean isStringLiteral(PsiElement element)
  {
    if (!(element instanceof ClLiteral))
    {
      return false;
    }
    ASTNode node = element.getNode();
    if (node == null)
    {
      return false;
    }
    ASTNode[] children = node.getChildren(null);
    return children.length == 1 && (children[0].getElementType() == Tokens.STRING_LITERAL);
  }

  public List<TextRange> select(PsiElement e, CharSequence editorText, int cursorOffset, Editor editor)
  {
    List<TextRange> result = super.select(e, editorText, cursorOffset, editor);

    TextRange range = e.getTextRange();
    if (range.getLength() <= 2)
    {
      result.add(range);
    }
    else
    {
      result.add(new TextRange(range.getStartOffset() + 1, range.getEndOffset() - 1));
    }
    return result;
  }
}