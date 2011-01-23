package schemely.psi.resolve.completion;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.resolve.processors.SymbolResolveProcessor;


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
