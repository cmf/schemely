package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.scheme.psi.SchemePsiElementImpl;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.SchemeAbbreviation;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public class SchemeAbbreviationImpl extends SchemePsiElementImpl implements SchemeAbbreviation
{
  public SchemeAbbreviationImpl(ASTNode node)
  {
    super(node);
  }

  @Override
  public String toString()
  {
    return "SchemeAbbreviation";
  }

  @Nullable
  public SchemePsiElement getQuotedElement()
  {
    return findChildByClass(SchemePsiElement.class);
  }
}
