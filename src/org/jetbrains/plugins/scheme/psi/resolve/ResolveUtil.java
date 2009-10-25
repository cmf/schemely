package org.jetbrains.plugins.scheme.psi.resolve;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.psi.api.SchemePsiElement;
import static org.jetbrains.plugins.scheme.psi.impl.SchemePsiElementBase.isWrongElement;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;


public abstract class ResolveUtil
{
  public static PsiElement treeWalkUp(SchemeIdentifier place)
  {
    PsiElement element = place;
    ResolveResult result = ResolveResult.CONTINUE;
    while ((element != null) && !result.isDone())
    {
      if (element instanceof SchemePsiElement)
      {
        SchemePsiElement schemePsiElement = (SchemePsiElement) element;
        result = schemePsiElement.resolve(place);
      }

      element = element.getContext(); //getParent
    }

    return result.getResult();
  }

  public static PsiElement[] mapToElements(SchemeResolveResult[] candidates)
  {
    PsiElement[] elements = new PsiElement[candidates.length];
    for (int i = 0; i < elements.length; i++)
    {
      elements[i] = candidates[i].getElement();
    }

    return elements;
  }

  public static PsiElement getNextNonLeafElement(@NotNull PsiElement element)
  {
    PsiElement next = element.getNextSibling();
    while ((next != null) && isWrongElement(next))
    {
      next = next.getNextSibling();
    }
    return next;
  }
}
