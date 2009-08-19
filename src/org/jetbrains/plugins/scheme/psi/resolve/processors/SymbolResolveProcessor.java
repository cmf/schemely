package org.jetbrains.plugins.scheme.psi.resolve.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.impl.list.ListDeclarations;
import org.jetbrains.plugins.scheme.psi.resolve.SchemeResolveResultImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ilyas
 */
public class SymbolResolveProcessor extends ResolveProcessor
{
  private final Set<PsiElement> myProcessedElements = new HashSet<PsiElement>();
  private final SchemeIdentifier place;

  public SymbolResolveProcessor(String myName, SchemeIdentifier place)
  {
    super(myName);
    this.place = place;
  }

  public boolean execute(PsiElement element, ResolveState resolveState)
  {
    if ((element instanceof PsiNamedElement) && !myProcessedElements.contains(element))
    {
      PsiNamedElement namedElement = (PsiNamedElement) element;

      if (!namedElement.equals(place))
      {
        myCandidates.add(new SchemeResolveResultImpl(namedElement));
      }
      myProcessedElements.add(namedElement);

      return !ListDeclarations.isLocal(element);
    }

    return true;
  }

  public String getName(ResolveState resolveState)
  {
    return myName;
  }

  public boolean shouldProcess(Class aClass)
  {
    return true;
  }
}
