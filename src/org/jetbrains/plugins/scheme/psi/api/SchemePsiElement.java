package org.jetbrains.plugins.scheme.psi.api;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveResult;

/**
 * @author Colin Fleming
 */
public interface SchemePsiElement extends PsiElement
{
  @NotNull
  ResolveResult resolve(SchemeIdentifier place);
}
