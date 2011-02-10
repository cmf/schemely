package schemely.psi.resolve;

import com.intellij.psi.PsiElement;


public class SchemeResolveResultImpl implements SchemeResolveResult
{
  private final PsiElement element;

  public SchemeResolveResultImpl(PsiElement element)
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
