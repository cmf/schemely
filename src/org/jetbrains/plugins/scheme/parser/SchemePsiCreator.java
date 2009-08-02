package org.jetbrains.plugins.scheme.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.scheme.psi.impl.*;
import org.jetbrains.plugins.scheme.psi.impl.defs.ClDefImpl;
import org.jetbrains.plugins.scheme.psi.impl.defs.ClDefnMethodImpl;
import org.jetbrains.plugins.scheme.psi.impl.list.ClListImpl;
import org.jetbrains.plugins.scheme.psi.impl.symbols.ClSymbolImpl;

/**
 * @author ilyas
 */
public class SchemePsiCreator
{
  public static PsiElement createElement(ASTNode node)
  {
    final IElementType elementType = node.getElementType();

    if (elementType == AST.LIST)
    {
      return new ClListImpl(node);
    }
    if (elementType == AST.VECTOR)
    {
      return new ClVectorImpl(node);
    }
    if (elementType == AST.QUOTED_FORM)
    {
      return new ClQuotedFormImpl(node);
    }
    if (elementType == AST.IDENTIFIER)
    {
      return new ClSymbolImpl(node);
    }
    if (elementType == AST.SYMBOL)
    {
      return new ClSymbolImpl(node);
    }
    if (elementType == AST.DEF)
    {
      return new ClDefImpl(node);
    }
    if (elementType == AST.DEFMETHOD)
    {
      return new ClDefnMethodImpl(node);
    }
    if (elementType == AST.BINDINGS)
    {
      return new ClBindings(node);
    }
    if (elementType == AST.KEYWORD)
    {
      return new ClKeyImpl(node);
    }
    if (elementType == AST.LITERAL)
    {
      return new ClLiteralImpl(node);
    }
    if (elementType == AST.BACKQUOTED_EXPRESSION)
    {
      return new ClBackQuotedExpression(node);
    }

    throw new Error("Unexpected ASTNode: " + node.getElementType());
  }
}
