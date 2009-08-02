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
    return e instanceof ClList || e instanceof ClVector;
  }

  @Override
  public List<TextRange> select(PsiElement element, CharSequence editorText, int cursorOffset, Editor editor)
  {
    List<TextRange> result = super.select(element, editorText, cursorOffset, editor);
    if (element instanceof ClBraced)
    {
      ClBraced list = (ClBraced) element;
      final PsiElement left = list.getFirstBrace();
      final PsiElement right = list.getLastBrace();
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
      final SchemePsiElement psi = (SchemePsiElement) element;
      final PsiElement fst = psi.getFirstNonLeafElement();
      final PsiElement lst = psi.getLastNonLeafElement();
      final int start = fst != null ? fst.getTextRange().getStartOffset() : psi.getTextRange().getStartOffset();
      final int end = lst != null ? lst.getTextRange().getEndOffset() : psi.getTextRange().getEndOffset();
      result.add(new TextRange(start, end));
    }


    return result;
  }
}
