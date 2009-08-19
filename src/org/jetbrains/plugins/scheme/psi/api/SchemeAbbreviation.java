package org.jetbrains.plugins.scheme.psi.api;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import com.intellij.psi.PsiElement;

/**
 * @author ilyas
 */
public interface SchemeAbbreviation extends PsiElement
{
  @Nullable
  SchemePsiElement getQuotedElement();
}
