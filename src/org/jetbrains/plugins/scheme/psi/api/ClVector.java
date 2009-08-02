package org.jetbrains.plugins.scheme.psi.api;

import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public interface ClVector extends SchemePsiElement, ClBraced, ClListLike
{
  ClSymbol[] getOddSymbols();
}
