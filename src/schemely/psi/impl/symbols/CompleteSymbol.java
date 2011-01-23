package schemely.psi.impl.symbols;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.lookup.LookupItemUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.MethodSignature;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashMap;
import com.intellij.util.containers.HashSet;
import schemely.SchemeIcons;
import schemely.psi.api.SchemePsiElement;
import schemely.psi.resolve.ResolveUtil;
import schemely.psi.resolve.SchemeResolveResult;
import schemely.psi.resolve.completion.CompletionProcessor;

import java.util.*;


public class CompleteSymbol
{
  public static Object[] getVariants(SchemeIdentifier symbol)
  {
    Set<LookupElement> variants = new HashSet<LookupElement>();

    PsiElement element = symbol.getContext();
    while ((element != null))
    {
      if (element instanceof SchemePsiElement)
      {
        SchemePsiElement schemePsiElement = (SchemePsiElement) element;
        variants.addAll(mapToLookupElements(schemePsiElement.getSymbolVariants(symbol)));
      }

      element = element.getContext(); //getParent
    }

    if (variants.size() == 0)
    {
      return PsiNamedElement.EMPTY_ARRAY;
    }

    return variants.toArray(new Object[variants.size()]);
  }

  private static Collection<LookupElement> mapToLookupElements(Collection<PsiElement> elements)
  {
    Collection<LookupElement> ret = new ArrayList<LookupElement>(elements.size());
    for (PsiElement element : elements)
    {
      ret.add(mapToLookupElement(element));
    }
    return ret;
  }

  private static LookupElement mapToLookupElement(PsiElement element)
  {
    // TODO
    if (element instanceof PsiNamedElement)
    {
      return LookupElementBuilder.create((PsiNamedElement) element);
    }
    return LookupElementBuilder.create(element, element.getText());
  }
}
