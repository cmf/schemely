package org.jetbrains.plugins.scheme.psi.api.symbols;

import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

/**
 * @author ilyas
 */
public interface ClSymbol extends SchemePsiElement, PsiPolyVariantReference, PsiNamedElement
{
  final ClSymbol[] EMPTY_ARRAY = new ClSymbol[0];

  @NotNull
  String getNameString();

  @Nullable
  PsiElement getReferenceNameElement();

  @Nullable
  String getReferenceName();

  ClSymbol getQualifierSymbol();

  boolean isQualified();

  @Nullable
  PsiElement getSeparatorToken();
}
