package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.impl.source.PsiFileWithStubSupport;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.file.SchemeFileType;
import org.jetbrains.plugins.scheme.psi.api.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.impl.list.SchemeList;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.impl.synthetic.SchemeSyntheticClassImpl;
import static org.jetbrains.plugins.scheme.psi.resolve.ResolveUtil.resolveFrom;
import org.jetbrains.plugins.scheme.psi.util.SchemePsiUtil;
import org.jetbrains.plugins.scheme.psi.util.SchemeTextUtil;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SchemeFile extends PsiFileBase implements PsiFile, PsiFileWithStubSupport, SchemePsiElement
{
  private PsiElement myContext = null;
  private PsiClass myClass;
  private boolean myScriptClassInitialized = false;

  @Override
  public String toString()
  {
    return "SchemeFile";
  }

  public SchemeFile(FileViewProvider viewProvider)
  {
    super(viewProvider, SchemeFileType.SCHEME_LANGUAGE);
  }

  @Override
  public PsiElement getContext()
  {
    if (myContext != null)
    {
      return myContext;
    }
    return super.getContext();
  }

  public PsiClass getDefinedClass()
  {
    if (!myScriptClassInitialized)
    {
      if (isScript())
      {
        myClass = new SchemeSyntheticClassImpl(this);
      }

      myScriptClassInitialized = true;
    }
    return myClass;
  }


  protected PsiFileImpl clone()
  {
    SchemeFile clone = (SchemeFile) super.clone();
    clone.myContext = myContext;
    return clone;
  }

  @NotNull
  public FileType getFileType()
  {
    return SchemeFileType.SCHEME_FILE_TYPE;
  }

  @NotNull
  public String getPackageName()
  {
    String ns = getNamespace();
    if (ns == null)
    {
      return "";
    }
    else
    {
      return SchemeTextUtil.getSymbolPrefix(ns);
    }
  }

  public boolean isScript()
  {
    return true;
  }

  @NotNull
  public ResolveResult resolve(SchemeIdentifier place)
  {
    PsiElement next = getFirstChild();
    while (next != null)
    {
      if ((PsiTreeUtil.findCommonParent(place, next) != next) && SchemeList.isDefinition(next))
      {
        ResolveResult identifier = resolveFrom(place, SchemeList.processDefineDeclaration((SchemeList) next, place));
        if (identifier.isDone())
        {
          return identifier;
        }
      }

      next = next.getNextSibling();
    }

    return ResolveResult.CONTINUE;
  }

  @Override
  public Collection<PsiElement> getSymbolVariants(SchemeIdentifier symbol)
  {
    Collection<PsiElement> ret = new ArrayList<PsiElement>();

    PsiElement next = getFirstChild();
    while (next != null)
    {
      if (SchemeList.isDefinition(next))
      {
        ret.addAll(SchemeList.processDefineDeclaration((SchemeList) next, symbol));
      }

      next = next.getNextSibling();
    }

    return ret;
  }

  @Override
  public int getQuotingLevel()
  {
    return 0;
  }

  public String getNamespace()
  {
    SchemeList ns = getNamespaceElement();
    if (ns == null)
    {
      return null;
    }
    SchemeIdentifier first = ns.findFirstChildByClass(SchemeIdentifier.class);
    if (first == null)
    {
      return null;
    }
    SchemeIdentifier snd = SchemePsiUtil.findNextSiblingByClass(first, SchemeIdentifier.class);
    if (snd == null)
    {
      return null;
    }

    return snd.getNameString();
  }

  public SchemeList getNamespaceElement()
  {
    // TODO CMF
    return null; //SchemePsiUtil.findFormByNameSet(this, SchemeParser.NS_TOKENS);
  }

  public PsiElement setClassName(@NonNls String s)
  {
    //todo implement me!
    return null;
  }
}
