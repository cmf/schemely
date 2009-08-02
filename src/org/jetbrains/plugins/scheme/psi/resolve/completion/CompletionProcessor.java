package org.jetbrains.plugins.scheme.psi.resolve.completion;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.plugins.scheme.psi.resolve.processors.SymbolResolveProcessor;

/**
 * @author ilyas
 */
public class CompletionProcessor extends SymbolResolveProcessor
{
  public CompletionProcessor(PsiElement myPlace)
  {
    super(null, myPlace, true, false);
  }

  public boolean execute(PsiElement element, ResolveState state)
  {
    super.execute(element, state);
    return true;
  }
}
