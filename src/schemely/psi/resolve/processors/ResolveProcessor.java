package schemely.psi.resolve.processors;

import com.intellij.psi.scope.ElementClassHint;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.containers.HashSet;
import schemely.psi.resolve.SchemeResolveResult;


public abstract class ResolveProcessor implements PsiScopeProcessor, NameHint, ElementClassHint
{
  protected HashSet<SchemeResolveResult> candidates = new HashSet<SchemeResolveResult>();
  protected final String name;

  public ResolveProcessor(String name)
  {
    this.name = name;
  }

  public SchemeResolveResult[] getCandidates()
  {
    return candidates.toArray(new SchemeResolveResult[candidates.size()]);
  }

  public <T> T getHint(Class<T> hintClass)
  {
    if (NameHint.class == hintClass && name != null)
    {
      return (T) this;
    }
    else if (ElementClassHint.class == hintClass)
    {
      return (T) this;
    }

    return null;
  }

  public void handleEvent(Event event, Object o)
  {
  }
}
