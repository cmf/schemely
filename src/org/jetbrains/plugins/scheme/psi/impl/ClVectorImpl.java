package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.scheme.psi.SchemePsiElementImpl;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.ClVector;
import org.jetbrains.plugins.scheme.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author ilyas
 */
public class ClVectorImpl extends SchemePsiElementImpl implements ClVector
{
  public ClVectorImpl(ASTNode node)
  {
    super(node, "ClVector");
  }

  @NotNull
  public PsiElement getFirstBrace()
  {
    PsiElement element = findChildByType(Tokens.LEFT_SQUARE);
    assert element != null;
    return element;
  }

  public PsiElement getLastBrace()
  {
    return findChildByType(Tokens.RIGHT_SQUARE);
  }

  public ClSymbol[] getAllSymbols()
  {
    return findChildrenByClass(ClSymbol.class);
  }

  public ClSymbol[] getOddSymbols()
  {
    final SchemePsiElement[] elems = findChildrenByClass(SchemePsiElement.class);
    final ArrayList<ClSymbol> res = new ArrayList<ClSymbol>();
    for (int i = 0; i < elems.length; i++)
    {
      SchemePsiElement elem = elems[i];
      if (i % 2 == 0 && elem instanceof ClSymbol)
      {
        res.add((ClSymbol) elem);
      }
    }
    return res.toArray(new ClSymbol[res.size()]);
  }
}
