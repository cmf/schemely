package schemely.psi.api;

import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.resolve.ResolveResult;

import java.util.Collection;

/**
 * @author Colin Fleming
 */
public interface SchemePsiElement extends PsiElement
{
  @NotNull
  ResolveResult resolve(SchemeIdentifier place);

  Collection<PsiElement> getSymbolVariants(SchemeIdentifier symbol);

  int getQuotingLevel();
}
