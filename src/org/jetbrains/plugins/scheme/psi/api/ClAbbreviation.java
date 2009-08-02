package org.jetbrains.plugins.scheme.psi.api;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;

/**
 * @author ilyas
 */
public interface ClAbbreviation
{
  @Nullable
  SchemePsiElement getQuotedElement();
}
