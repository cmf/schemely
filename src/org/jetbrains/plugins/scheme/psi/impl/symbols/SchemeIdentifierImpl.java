package org.jetbrains.plugins.scheme.psi.impl.symbols;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.SchemeIcons;
import org.jetbrains.plugins.scheme.lexer.TokenSets;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.plugins.scheme.psi.SchemePsiElementImpl;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.scheme.psi.resolve.SchemeResolveResult;
import org.jetbrains.plugins.scheme.psi.resolve.processors.ResolveProcessor;
import org.jetbrains.plugins.scheme.psi.resolve.processors.SymbolResolveProcessor;
import org.jetbrains.plugins.scheme.psi.util.SchemePsiElementFactory;

import javax.swing.*;

/**
 * @author ilyas
 */
public class SchemeIdentifierImpl extends SchemePsiElementImpl implements SchemeIdentifier
{
  public SchemeIdentifierImpl(ASTNode node)
  {
    super(node);
  }

  @Override
  public PsiReference getReference()
  {
    return this;
  }

  @Override
  public String toString()
  {
    return "SchemeIdentifier";
  }

  public PsiElement getElement()
  {
    return this;
  }

  public TextRange getRangeInElement()
  {
    PsiElement refNameElement = getReferenceNameElement();
    if (refNameElement != null)
    {
      int offsetInParent = refNameElement.getStartOffsetInParent();
      return new TextRange(offsetInParent, offsetInParent + refNameElement.getTextLength());
    }
    return new TextRange(0, getTextLength());
  }

  @Nullable
  public PsiElement getReferenceNameElement()
  {
    ASTNode lastChild = getNode().getLastChildNode();
    if (lastChild == null)
    {
      return null;
    }
    for (IElementType elementType : TokenSets.REFERENCE_NAMES.getTypes())
    {
      if (lastChild.getElementType() == elementType)
      {
        return lastChild.getPsi();
      }
    }

    return null;
  }

  @Nullable
  public String getReferenceName()
  {
    PsiElement nameElement = getReferenceNameElement();
    if (nameElement != null)
    {
      ASTNode node = nameElement.getNode();
      if ((node != null) && (node.getElementType() == Tokens.IDENTIFIER))
      {
        return nameElement.getText();
      }
    }
    return null;
  }

  @NotNull
  public ResolveResult[] multiResolve(boolean incomplete)
  {
    // TODO this is only for debug
    getManager().getResolveCache().clearResolveCaches(this);
    return getManager().getResolveCache().resolveWithCaching(this, RESOLVER, true, incomplete);
  }

  public PsiElement setName(@NotNull @NonNls String newName) throws IncorrectOperationException
  {
    ASTNode newNode = SchemePsiElementFactory.getInstance(getProject()).createSymbolNodeFromText(newName);
    ASTNode parentNode = getParent().getNode();
    if (parentNode != null)
    {
      parentNode.replaceChild(getNode(), newNode);
    }
    return newNode.getPsi();
  }

  @Override
  public Icon getIcon(int flags)
  {
    return SchemeIcons.SYMBOL;
  }

  @Override
  public ItemPresentation getPresentation()
  {
    return new ItemPresentation()
    {
      public String getPresentableText()
      {
        String name = getName();
        return name == null ? "<undefined>" : name;
      }

      @Nullable
      public String getLocationString()
      {
        String name = getContainingFile().getName();
        //todo show namespace
        return "(in " + name + ")";
      }

      @Nullable
      public Icon getIcon(boolean open)
      {
        return SchemeIdentifierImpl.this.getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
      }

      @Nullable
      public TextAttributesKey getTextAttributesKey()
      {
        return null;
      }
    };
  }


  public static class MyResolver implements ResolveCache.PolyVariantResolver<SchemeIdentifier>
  {
    public ResolveResult[] resolve(SchemeIdentifier symbol, boolean incompleteCode)
    {
      String name = symbol.getReferenceName();
      if (name == null)
      {
        return null;
      }

      String myName = StringUtil.trimEnd(name, ".");
      ResolveProcessor processor = new SymbolResolveProcessor(myName, symbol);

      SchemeIdentifier qualifier = symbol.getQualifierSymbol();
      if (qualifier == null)
      {
        ResolveUtil.treeWalkUp(symbol, processor);
      }
      //      else
      //      {
      //        for (ResolveResult result : qualifier.multiResolve(false))
      //        {
      //          final PsiElement element = result.getElement();
      //          if (element != null)
      //          {
      //            final PsiElement sep = symbol.getSeparatorToken();
      //            if (sep != null)
      //            {
      //              if ("/".equals(sep.getText()))
      //              {
      //                //get class elemets
      //                if (element instanceof PsiClass)
      //                {
      //                  element.processDeclarations(processor, ResolveState.initial(), null, symbol);
      //                }
      //              }
      //              else if (".".equals(sep.getText()))
      //              {
      //                element.processDeclarations(processor, ResolveState.initial(), null, symbol);
      //              }
      //            }
      //          }
      //        }
      //      }

      SchemeResolveResult[] candidates = processor.getCandidates();
      if (candidates.length > 0)
      {
        return candidates;
      }

      return SchemeResolveResult.EMPTY_ARRAY;
    }

  }

  public SchemeIdentifier getQualifierSymbol()
  {
    return findChildByClass(SchemeIdentifier.class);
  }

  @Override
  public String getName()
  {
    return getNameString();
  }

  private static final MyResolver RESOLVER = new MyResolver();

  public PsiElement resolve()
  {
    // TODO this is only for debug
    getManager().getResolveCache().clearResolveCaches(this);
    ResolveResult[] results = getManager().getResolveCache().resolveWithCaching(this, RESOLVER, false, false);
    return results.length == 1 ? results[0].getElement() : null;
  }

  public String getCanonicalText()
  {
    return null;
  }

  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
  {
    PsiElement nameElement = getReferenceNameElement();
    if (nameElement != null)
    {
      ASTNode node = nameElement.getNode();
      ASTNode newNameNode = SchemePsiElementFactory.getInstance(getProject()).createSymbolNodeFromText(newElementName);
      assert newNameNode != null && node != null;
      node.getTreeParent().replaceChild(node, newNameNode);
    }
    return this;
  }

  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
  {
    //todo implement me!
    return this;
  }

  public boolean isReferenceTo(PsiElement element)
  {
    return resolve() == element;
  }

  public Object[] getVariants()
  {
    return CompleteSymbol.getVariants(this);
  }

  public boolean isSoft()
  {
    return false;
  }

  @NotNull
  public String getNameString()
  {
    return getText();
  }

}
