package org.jetbrains.plugins.scheme.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.scheme.psi.impl.SchemeAbbreviationImpl;
import org.jetbrains.plugins.scheme.psi.impl.SchemeLiteralImpl;
import org.jetbrains.plugins.scheme.psi.impl.SchemeVectorImpl;
import org.jetbrains.plugins.scheme.psi.impl.list.SchemeListImpl;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifierImpl;

/**
 * @author ilyas
 */
public class SchemePsiCreator
{
  public static PsiElement createElement(ASTNode node)
  {
    IElementType elementType = node.getElementType();

    if (elementType == AST.LIST)
    {
      return new SchemeListImpl(node);
    }
    if (elementType == AST.VECTOR)
    {
      return new SchemeVectorImpl(node);
    }
    if (elementType == AST.ABBREVIATION)
    {
      return new SchemeAbbreviationImpl(node);
    }
    if (elementType == AST.IDENTIFIER)
    {
      return new SchemeIdentifierImpl(node);
    }
    if (elementType == AST.LITERAL)
    {
      return new SchemeLiteralImpl(node);
    }

    throw new Error("Unexpected ASTNode: " + node.getElementType());
  }
}
