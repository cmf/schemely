package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.scheme.lexer.Tokens;


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

  @Override
  public int getQuotingLevel()
  {
    ASTNode child = getNode().findChildByType(Tokens.PREFIXES);
    if (child != null)
    {
      IElementType type = child.getElementType();
      if ((type == Tokens.QUOTE_MARK) || (type == Tokens.BACKQUOTE))
      {
        return 1;
      }
      else if (type == Tokens.COMMA || type == Tokens.COMMA_AT)
      {
        return -1;
      }
    }

    // Should never happen
    return 0;
  }
}
