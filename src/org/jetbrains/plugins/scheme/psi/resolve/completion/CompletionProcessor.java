package org.jetbrains.plugins.scheme.psi.resolve.completion;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.plugins.scheme.psi.resolve.processors.SymbolResolveProcessor;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;

/**
 * @author ilyas
 */
public class CompletionProcessor extends SymbolResolveProcessor
{
  public CompletionProcessor(SchemeIdentifier place)
  {
    super(null, place);
  }

  public boolean execute(PsiElement element, ResolveState state)
  {
    super.execute(element, state);
    return true;
  }
}
