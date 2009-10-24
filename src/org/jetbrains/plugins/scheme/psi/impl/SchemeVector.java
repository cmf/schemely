package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.plugins.scheme.psi.impl.SchemePsiElementBase;
import org.jetbrains.plugins.scheme.psi.api.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.impl.list.SchemeList;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;

import java.util.ArrayList;


public class SchemeVector extends SchemePsiElementBase
{
  public SchemeVector(ASTNode node)
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
    SchemePsiElementBase[] elems = findChildrenByClass(SchemePsiElementBase.class);
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
