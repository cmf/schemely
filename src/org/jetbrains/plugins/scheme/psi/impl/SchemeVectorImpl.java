package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.scheme.psi.SchemePsiElementImpl;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.SchemeVector;
import org.jetbrains.plugins.scheme.psi.api.SchemeList;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author ilyas
 */
public class SchemeVectorImpl extends SchemePsiElementImpl implements SchemeVector
{
  public SchemeVectorImpl(ASTNode node)
  {
    super(node, "SchemeVector");
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

  public SchemeIdentifier[] getAllSymbols()
  {
    return findChildrenByClass(SchemeIdentifier.class);
  }

  public SchemeList[] getSubLists()
  {
    return findChildrenByClass(SchemeList.class);
  }

  public SchemeIdentifier[] getOddSymbols()
  {
    SchemePsiElement[] elems = findChildrenByClass(SchemePsiElement.class);
    ArrayList<SchemeIdentifier> res = new ArrayList<SchemeIdentifier>();
    for (int i = 0; i < elems.length; i++)
    {
      SchemePsiElement elem = elems[i];
      if (i % 2 == 0 && elem instanceof SchemeIdentifier)
      {
        res.add((SchemeIdentifier) elem);
      }
    }
    return res.toArray(new SchemeIdentifier[res.size()]);
  }
}
