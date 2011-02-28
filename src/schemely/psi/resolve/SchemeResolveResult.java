package schemely.psi.resolve;

import com.intellij.psi.PsiElement;


public class SchemeResolveResult implements com.intellij.psi.ResolveResult
{
  private final PsiElement element;

  public SchemeResolveResult(PsiElement element)
  {
    this.element = element;
  }

  public PsiElement getElement()
  {
    return element;
  }

  public boolean isValidResult()
  {
    return true;
  }
}
