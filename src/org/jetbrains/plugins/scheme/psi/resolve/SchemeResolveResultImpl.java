package org.jetbrains.plugins.scheme.psi.resolve;

import com.intellij.psi.PsiElement;

/**
 * @author ilyas
 */
public class SchemeResolveResultImpl implements SchemeResolveResult
{
  private final PsiElement myElement;

  public SchemeResolveResultImpl(PsiElement myElement)
  {
    this.myElement = myElement;
  }

  public PsiElement getElement()
  {
    return myElement;
  }

  public boolean isValidResult()
  {
    return true;
  }
}
