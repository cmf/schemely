package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.scheme.psi.SchemePsiElementImpl;
import org.jetbrains.plugins.scheme.psi.api.ClLiteral;

/**
 * @author ilyas
 */
public class ClLiteralImpl extends SchemePsiElementImpl implements ClLiteral
{
  public ClLiteralImpl(ASTNode node)
  {
    super(node, "ClLiteral");
  }
}
