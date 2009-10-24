package org.jetbrains.plugins.scheme.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.scheme.psi.impl.SchemeAbbreviation;
import org.jetbrains.plugins.scheme.psi.impl.SchemeLiteral;
import org.jetbrains.plugins.scheme.psi.impl.SchemeVector;
import org.jetbrains.plugins.scheme.psi.impl.list.SchemeList;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;


public class SchemePsiCreator
{
  public static PsiElement createElement(ASTNode node)
  {
    IElementType elementType = node.getElementType();

    if (elementType == AST.LIST)
    {
      return new SchemeList(node);
    }
    if (elementType == AST.VECTOR)
    {
      return new SchemeVector(node);
    }
    if (elementType == AST.ABBREVIATION)
    {
      return new SchemeAbbreviation(node);
    }
    if (elementType == AST.IDENTIFIER)
    {
      return new SchemeIdentifier(node);
    }
    if (elementType == AST.LITERAL)
    {
      return new SchemeLiteral(node);
    }

    throw new Error("Unexpected ASTNode: " + node.getElementType());
  }
}
