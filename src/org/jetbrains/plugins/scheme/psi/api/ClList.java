package org.jetbrains.plugins.scheme.psi.api;

import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.symbols.ClSymbol;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;

/**
 * @author ilyas
 */
public interface ClList extends SchemePsiElement, ClBraced, ClListLike
{
  @Nullable
  String getPresentableText();

  @Nullable
  ClSymbol getFirstSymbol();

  @Nullable
  PsiElement getSecondNonLeafElement();

  @Nullable
  String getHeadText();

}
