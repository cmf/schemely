package schemely.psi.impl.symbols;

import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.containers.HashSet;
import schemely.psi.api.SchemePsiElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


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
      if (element.getText().indexOf(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED) < 0)
      {
        ret.add(mapToLookupElement(element));
      }
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
