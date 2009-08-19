package org.jetbrains.plugins.scheme.editor.selection;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.*;

import java.util.List;

/**
 * @author ilyas
 */
public class SchemeListSelectioner extends SchemeBasicSelectioner
{
  public boolean canSelect(PsiElement e)
  {
    return e instanceof SchemeList || e instanceof SchemeVector;
  }

  @Override
  public List<TextRange> select(PsiElement element, CharSequence editorText, int cursorOffset, Editor editor)
  {
    List<TextRange> result = super.select(element, editorText, cursorOffset, editor);
    if (element instanceof SchemeBraced)
    {
      SchemeBraced list = (SchemeBraced) element;
      PsiElement left = list.getFirstBrace();
      PsiElement right = list.getLastBrace();
      if (right != null)
      {
        result.add(new TextRange(left.getTextRange().getStartOffset(), right.getTextRange().getEndOffset()));
      }
      else
      {
        result.add(new TextRange(left.getTextRange().getStartOffset(), element.getTextRange().getEndOffset()));
      }
    }
    if (element instanceof SchemePsiElement)
    {
      SchemePsiElement psi = (SchemePsiElement) element;
      PsiElement fst = psi.getFirstNonLeafElement();
      PsiElement lst = psi.getLastNonLeafElement();
      int start = fst != null ? fst.getTextRange().getStartOffset() : psi.getTextRange().getStartOffset();
      int end = lst != null ? lst.getTextRange().getEndOffset() : psi.getTextRange().getEndOffset();
      result.add(new TextRange(start, end));
    }


    return result;
  }
}
