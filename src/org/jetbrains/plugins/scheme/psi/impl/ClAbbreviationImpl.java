package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.scheme.psi.SchemePsiElementImpl;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.ClAbbreviation;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public class ClAbbreviationImpl extends SchemePsiElementImpl implements ClAbbreviation
{
  public ClAbbreviationImpl(ASTNode node)
  {
    super(node);
  }

  @Override
  public String toString()
  {
    return "ClAbbreviation";
  }

  @Nullable
  public SchemePsiElement getQuotedElement()
  {
    return findChildByClass(SchemePsiElement.class);
  }
}
