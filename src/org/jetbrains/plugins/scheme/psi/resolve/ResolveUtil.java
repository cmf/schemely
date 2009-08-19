package org.jetbrains.plugins.scheme.psi.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import static org.jetbrains.plugins.scheme.psi.SchemeBaseElementImpl.isWrongElement;

/**
 * @author ilyas
 */
public abstract class ResolveUtil
{
  public static boolean treeWalkUp(PsiElement place, PsiScopeProcessor processor)
  {
    PsiElement lastParent = null;
    PsiElement run = place;
    while (run != null)
    {
      if (!run.processDeclarations(processor, ResolveState.initial(), lastParent, place))
      {
        return false;
      }
      lastParent = run;
      run = run.getContext(); //same as getParent
    }

    return true;
  }

  public static boolean processElement(PsiScopeProcessor processor, PsiNamedElement namedElement)
  {
    if (namedElement == null)
    {
      return true;
    }
    NameHint nameHint = processor.getHint(NameHint.class);
    String name = nameHint == null ? null : nameHint.getName(ResolveState.initial());
    if ((name == null) || name.equals(namedElement.getName()))
    {
      return processor.execute(namedElement, ResolveState.initial());
    }
    return true;
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

  public static PsiElement getPrevNonLeafElement(@NotNull PsiElement element)
  {
    PsiElement next = element.getPrevSibling();
    while ((next != null) && isWrongElement(next))
    {
      next = next.getPrevSibling();
    }
    return next;
  }
}
