package org.jetbrains.plugins.scheme.psi.resolve.processors;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.plugins.scheme.psi.impl.list.SchemeList;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.resolve.SchemeResolveResultImpl;

import java.util.HashSet;
import java.util.Set;


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

      return !SchemeList.isLocal(element);
    }

    return true;
  }

  @Override
  public <T> T getHint(Key<T> hintKey)
  {
    return null;
  }

  public String getName(ResolveState resolveState)
  {
    return myName;
  }

  @Override
  public boolean shouldProcess(DeclaractionKind kind)
  {
    return true;
  }
}
