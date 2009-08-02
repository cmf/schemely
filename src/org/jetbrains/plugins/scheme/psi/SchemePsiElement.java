package org.jetbrains.plugins.scheme.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public interface SchemePsiElement extends PsiElement
{
  @Nullable
  PsiElement getFirstNonLeafElement();

  @Nullable
  PsiElement getLastNonLeafElement();

}
