package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.scheme.psi.SchemePsiElementImpl;
import org.jetbrains.plugins.scheme.psi.api.SchemeLiteral;

/**
 * @author ilyas
 */
public class SchemeLiteralImpl extends SchemePsiElementImpl implements SchemeLiteral
{
  public SchemeLiteralImpl(ASTNode node)
  {
    super(node, "SchemeLiteral");
  }
}
