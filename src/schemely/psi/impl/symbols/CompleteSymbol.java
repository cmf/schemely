package schemely.psi.impl.symbols;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.util.containers.HashSet;
import schemely.psi.resolve.SchemeResolveResult;
import schemely.psi.resolve.completion.CompletionProcessor;

import java.util.Set;


public class CompleteSymbol
{
  public static Object[] getVariants(SchemeIdentifier symbol)
  {

    PsiElement lastParent = null;
    PsiElement current = symbol;
    CompletionProcessor processor = new CompletionProcessor();
    while (current != null)
    {
      if (!current.processDeclarations(processor, ResolveState.initial(), lastParent, symbol))
      {
        break;
      }
      lastParent = current;
      current = current.getContext();
    }

    SchemeResolveResult[] candidates = processor.getCandidates();
    if (candidates.length == 0)
    {
      return PsiNamedElement.EMPTY_ARRAY;
    }

    Set<LookupElement> variants = new HashSet<LookupElement>();

    for (SchemeResolveResult candidate : candidates)
    {
      variants.add(mapToLookupElement(candidate.getElement()));
    }

    return variants.toArray(new Object[variants.size()]);
  }

  private static LookupElement mapToLookupElement(PsiElement element)
  {
    if (element instanceof PsiNamedElement)
    {
      return LookupElementBuilder.create((PsiNamedElement) element);
    }
    return LookupElementBuilder.create(element, element.getText());
  }
}
