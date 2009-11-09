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
  public static final
  Set<String>
    IMPLICIT_NAMES =
    new HashSet<String>(Arrays.asList("define",
                                      "new",
                                      "throw",
                                      "quote",
                                      "quasiquote",
                                      "unquote",
                                      "unquote-splicing",
                                      "lambda",
                                      "define",
                                      "define-syntax",
                                      "if",
                                      "else",
                                      "let",
                                      "let*",
                                      "letrec",
                                      "set!",
                                      "begin",
                                      "cond",
                                      "and",
                                      "or",
                                      "case",
                                      "do",
                                      "delay",
                                      "let-syntax",
                                      "letrec-syntax"));

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

//    PsiElement second = list.getSecondNonLeafElement();
//    StringBuffer buffer = new StringBuffer();
//    buffer.append(first == null ? "null" : '"' + first.getReferenceName() + '"');
//    buffer.append(" ");
//    buffer.append(second == null ? "null" : '"' + second.getText() + '"');
//    System.out.println(buffer.toString());

    if ((first != null) && IMPLICIT_NAMES.contains(first.getReferenceName()))
    {
      Annotation annotation = holder.createInfoAnnotation(first, null);
      annotation.setTextAttributes(SchemeSyntaxHighlighter.KEYWORD);
    }
  }
}
