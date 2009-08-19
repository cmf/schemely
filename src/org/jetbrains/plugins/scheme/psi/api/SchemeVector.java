package org.jetbrains.plugins.scheme.psi.api;

import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;

/**
 * @author ilyas
 */
public interface SchemeVector extends SchemePsiElement, SchemeBraced, SchemeListLike
{
  SchemeIdentifier[] getOddSymbols();
}
