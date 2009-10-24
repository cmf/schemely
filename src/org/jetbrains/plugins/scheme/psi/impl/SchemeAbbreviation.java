package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;


public class SchemeAbbreviation extends SchemePsiElementBase
{
  public SchemeAbbreviation(ASTNode node)
  {
    super(node, "Abbreviation");
  }

  @Override
  public String toString()
  {
    return "SchemeAbbreviation";
  }

}
