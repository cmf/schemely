package org.jetbrains.plugins.scheme.psi;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.PsiComment;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
 */
public abstract class SchemeBaseElementImpl<T extends StubElement> extends StubBasedPsiElementBase<T> implements
                                                                                                      SchemePsiElement
{
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

  public SchemeBaseElementImpl(T stub, @org.jetbrains.annotations.NotNull IStubElementType nodeType)
  {
    super(stub, nodeType);
  }

  public SchemeBaseElementImpl(ASTNode node)
  {
    super(node);
  }
}
