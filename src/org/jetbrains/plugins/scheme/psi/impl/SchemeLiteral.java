package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;


public class SchemeLiteral extends SchemePsiElementBase
{
  public SchemeLiteral(ASTNode node)
  {
    super(node, "SchemeLiteral");
  }
}
