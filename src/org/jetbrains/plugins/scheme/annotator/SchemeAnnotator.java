package org.jetbrains.plugins.scheme.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.HashSet;
import org.jetbrains.plugins.scheme.highlighter.SchemeSyntaxHighlighter;
import org.jetbrains.plugins.scheme.psi.impl.list.SchemeList;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;

import java.util.Arrays;
import java.util.Set;


public class SchemeAnnotator implements Annotator
{
  public static final Set<String> IMPLICIT_NAMES = new HashSet<String>();

  static
  {
    IMPLICIT_NAMES.addAll(Arrays.asList("def", "new", "throw"));
  }

  public void annotate(PsiElement element, AnnotationHolder holder)
  {
    if (element instanceof SchemeList)
    {
      annotateList((SchemeList) element, holder);
    }
  }

  private void annotateList(SchemeList list, AnnotationHolder holder)
  {
    SchemeIdentifier first = list.getFirstIdentifier();
    if (((first != null) && (first.resolve() != null)) || IMPLICIT_NAMES.contains(list.getHeadText()))
    {
      Annotation annotation = holder.createInfoAnnotation(first, null);
      annotation.setTextAttributes(SchemeSyntaxHighlighter.DEF);
    }
  }
}
