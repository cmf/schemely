package org.jetbrains.plugins.scheme.psi.resolve;

import com.intellij.psi.PsiElement;

/**
 * @author ilyas
 */
public class SchemeResolveResultImpl implements SchemeResolveResult
{
  private final PsiElement myElement;
  private final boolean myIsAccessible;

  public SchemeResolveResultImpl(PsiElement myElement, boolean myIsAccessible)
  {
    this.myElement = myElement;
    this.myIsAccessible = myIsAccessible;
  }


  public PsiElement getElement()
  {
    return myElement;
  }

  public boolean isValidResult()
  {
    return isAccessible();
  }

  public boolean isAccessible()
  {
    return myIsAccessible;
  }


}
