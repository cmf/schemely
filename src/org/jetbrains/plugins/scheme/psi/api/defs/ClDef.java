package org.jetbrains.plugins.scheme.psi.api.defs;

import com.intellij.psi.PsiNamedElement;
import com.intellij.navigation.NavigationItem;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.scheme.psi.api.ClList;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public interface ClDef extends ClList, PsiNamedElement, NavigationItem
{
  @Nullable
  ClSymbol getNameSymbol();

  String getDefinedName();

  String getPresentationText();

  String getParameterString();
}
