package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.scheme.psi.SchemePsiElementImpl;
import org.jetbrains.plugins.scheme.psi.api.ClKeyword;

/**
 * @author ilyas
 */
public class ClKeyImpl extends SchemePsiElementImpl implements ClKeyword
{
  public ClKeyImpl(ASTNode node)
  {
    super(node, "ClKeyword");
  }
}
