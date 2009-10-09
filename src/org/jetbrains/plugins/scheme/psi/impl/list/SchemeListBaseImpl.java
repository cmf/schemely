package org.jetbrains.plugins.scheme.psi.impl.list;

import org.jetbrains.plugins.scheme.psi.SchemeBaseElementImpl;
import org.jetbrains.plugins.scheme.psi.util.SchemePsiUtil;
import org.jetbrains.plugins.scheme.psi.api.SchemeList;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
 */
public abstract class SchemeListBaseImpl<T extends NamedStub> extends SchemeBaseElementImpl<T> implements SchemeList
{
  public SchemeListBaseImpl(ASTNode node)
  {
    super(node);
  }

  @Nullable
  public String getPresentableText()
  {
    SchemeIdentifier first = findChildByClass(SchemeIdentifier.class);
    if (first == null)
    {
      return null;
    }
    String text1 = getHeadText();
    PsiElement next = SchemePsiUtil.findNextSiblingByClass(first, SchemeIdentifier.class);
    if (next == null)
    {
      return text1;
    }
    else
    {
      return text1 + " " + next.getText();
    }
  }

  @Nullable
  public String getHeadText()
  {
    SchemeIdentifier first = findChildByClass(SchemeIdentifier.class);
    if (first == null)
    {
      return null;
    }
    return first.getText();
  }

  public boolean isImproper()
  {
    PsiElement dot = findChildByType(Tokens.DOT);
    return dot != null;
  }

  @Nullable
  public SchemeIdentifier getFirstIdentifier()
  {
    PsiElement child = getFirstChild();
    while (child instanceof LeafPsiElement)
    {
      child = child.getNextSibling();
    }
    if (child instanceof SchemeIdentifier)
    {
      return (SchemeIdentifier) child;
    }
    return null;
  }

  @NotNull
  public PsiElement getFirstBrace()
  {
    PsiElement element = findChildByType(Tokens.LEFT_PAREN);
    assert element != null;
    return element;
  }

  public PsiElement getSecondNonLeafElement()
  {
    PsiElement first = getFirstChild();
    while ((first != null) && isWrongElement(first))
    {
      first = first.getNextSibling();
    }
    PsiElement second = first.getNextSibling();
    while (second != null && isWrongElement(second))
    {
      second = second.getNextSibling();
    }
    return second;
  }

  public PsiElement getLastBrace()
  {
    return findChildByType(Tokens.RIGHT_PAREN);
  }


  public SchemeIdentifier[] getAllSymbols()
  {
    return findChildrenByClass(SchemeIdentifier.class);
  }

  public SchemeList[] getSubLists()
  {
    return findChildrenByClass(SchemeList.class);
  }
}
