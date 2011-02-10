package schemely.psi.resolve.processors;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.resolve.SchemeResolveResultImpl;

import java.util.HashSet;
import java.util.Set;


public class SymbolResolveProcessor extends ResolveProcessor
{
  private final Set<PsiElement> processedElements = new HashSet<PsiElement>();
  private final SchemeIdentifier place;

  public SymbolResolveProcessor(String name, SchemeIdentifier place)
  {
    super(name);
    this.place = place;
  }

  public boolean execute(PsiElement element, ResolveState resolveState)
  {
    if ((element instanceof PsiNamedElement) && !processedElements.contains(element))
    {
      PsiNamedElement namedElement = (PsiNamedElement) element;

      if (!namedElement.equals(place))
      {
        candidates.add(new SchemeResolveResultImpl(namedElement));
      }
      processedElements.add(namedElement);

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
    return name;
  }

  @Override
  public boolean shouldProcess(DeclaractionKind kind)
  {
    return true;
  }
}
