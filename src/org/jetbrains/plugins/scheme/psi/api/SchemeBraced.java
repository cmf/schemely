package org.jetbrains.plugins.scheme.psi.api;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface SchemeBraced
{
  @NotNull
  PsiElement getFirstBrace();

  @Nullable
  PsiElement getLastBrace();
}
