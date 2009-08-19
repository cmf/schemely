package org.jetbrains.plugins.scheme.psi.api;

import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;

/**
 * @author ilyas
 */
public interface SchemeList extends SchemePsiElement, SchemeBraced, SchemeListLike
{
  @Nullable
  String getPresentableText();

  @Nullable
  SchemeIdentifier getFirstIdentifier();

  @Nullable
  PsiElement getSecondNonLeafElement();

  @Nullable
  String getHeadText();

  boolean isImproper();
}
