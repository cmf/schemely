package schemely.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;
import schemely.psi.api.SchemePsiElement;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.resolve.ResolveResult;

import java.util.Collection;
import java.util.Collections;


public abstract class SchemePsiElementBase extends ASTWrapperPsiElement implements SchemePsiElement
{
  final private String name;

  public SchemePsiElementBase(@NotNull ASTNode astNode, String name)
  {
    super(astNode);
    this.name = name;
  }

  public static boolean isWrongElement(PsiElement element)
  {
    return element == null ||
           (element instanceof LeafPsiElement || element instanceof PsiWhiteSpace || element instanceof PsiComment);
  }

  public PsiElement getFirstNonLeafElement()
  {
    PsiElement first = getFirstChild();
    while (first != null && isWrongElement(first))
    {
      first = first.getNextSibling();
    }
    return first;
  }

  public PsiElement getLastNonLeafElement()
  {
    PsiElement lastChild = getLastChild();
    while (lastChild != null && isWrongElement(lastChild))
    {
      lastChild = lastChild.getPrevSibling();
    }
    return lastChild;
  }

  public <T> T findFirstChildByClass(Class<T> aClass)
  {
    PsiElement element = getFirstChild();
    while (element != null && !aClass.isInstance(element))
    {
      element = element.getNextSibling();
    }
    return aClass.cast(element);
  }

  public <T> T findFirstSiblingByClass(Class<T> aClass)
  {
    PsiElement element = getNextSibling();
    while (element != null && !aClass.isInstance(element))
    {
      element = element.getNextSibling();
    }
    return aClass.cast(element);
  }

  @Override
  public String toString()
  {
    return name == null ? super.toString() : name;
  }

  @NotNull
  public ResolveResult resolve(SchemeIdentifier place)
  {
    return ResolveResult.CONTINUE;
  }

  @Override
  public Collection<PsiElement> getSymbolVariants(SchemeIdentifier symbol)
  {
    return Collections.emptyList();
  }

  @Override
  public int getQuotingLevel()
  {
    return 0;
  }
}
