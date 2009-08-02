package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.scheme.psi.SchemePsiElementImpl;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.ClQuotedForm;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public class ClQuotedFormImpl extends SchemePsiElementImpl implements ClQuotedForm
{
  public ClQuotedFormImpl(ASTNode node)
  {
    super(node);
  }

  @Override
  public String toString()
  {
    return "ClQuotedForm";
  }

  @Nullable
  public SchemePsiElement getQuotedElement()
  {
    return findChildByClass(SchemePsiElement.class);
  }


}
