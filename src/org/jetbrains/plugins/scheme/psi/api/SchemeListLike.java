package org.jetbrains.plugins.scheme.psi.api;

import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;

/**
 * @author ilyas
 */
public interface SchemeListLike extends SchemePsiElement
{
  SchemeIdentifier[] getAllSymbols();

  SchemeList[] getSubLists();

  <T> T findFirstChildByClass(Class<T> aClass);
}
