package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.psi.api.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveResult;


public abstract class SchemePsiElementBase extends ASTWrapperPsiElement implements SchemePsiElement
{
  final private String myName;

  public SchemePsiElementBase(@NotNull ASTNode astNode, String myName)
  {
    super(astNode);
    this.myName = myName;
  }

  public static boolean isWrongElement(PsiElement element)
  {
    return element == null ||
           (element instanceof LeafPsiElement || element instanceof PsiWhiteSpace || element instanceof PsiComment);
  }

  public PsiElement getFirstNonLeafElement()
  {
    PsiElement first = getFirstChild();
    while (first != null && isWrongElement(first))
    {
      first = first.getNextSibling();
    }
    return first;
  }

  public PsiElement getLastNonLeafElement()
  {
    PsiElement lastChild = getLastChild();
    while (lastChild != null && isWrongElement(lastChild))
    {
      lastChild = lastChild.getPrevSibling();
    }
    return lastChild;
  }

  public <T> T findFirstChildByClass(Class<T> aClass)
  {
    PsiElement element = getFirstChild();
    while (element != null && !aClass.isInstance(element))
    {
      element = element.getNextSibling();
    }
    return aClass.cast(element);
  }

  @Override
  public String toString()
  {
    return myName == null ? super.toString() : myName;
  }

  @NotNull
  public ResolveResult resolve(SchemeIdentifier place)
  {
    return ResolveResult.CONTINUE;
  }
}
