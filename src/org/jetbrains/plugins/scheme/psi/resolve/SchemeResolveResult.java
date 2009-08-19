package org.jetbrains.plugins.scheme.psi.resolve;

import com.intellij.psi.ResolveResult;

/**
 * @author ilyas
 */
public interface SchemeResolveResult extends ResolveResult
{
  public SchemeResolveResult[] EMPTY_ARRAY = new SchemeResolveResult[0];
}
