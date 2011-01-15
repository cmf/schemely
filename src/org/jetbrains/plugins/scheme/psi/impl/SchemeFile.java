package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.impl.source.PsiFileWithStubSupport;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.file.SchemeFileType;
import org.jetbrains.plugins.scheme.psi.api.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.impl.list.SchemeList;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.impl.synthetic.SchemeSyntheticClassImpl;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveResult;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.scheme.psi.util.SchemePsiUtil;
import org.jetbrains.plugins.scheme.psi.util.SchemeTextUtil;

import java.util.ArrayList;
import java.util.Collection;

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
        ResolveResult identifier =
          ResolveUtil.resolveFrom(place, SchemeList.processDefineDeclaration((SchemeList) next, place));
        if (identifier.isDone())
        {
          return identifier;
        }
      }

      next = next.getNextSibling();
    }

    String url = VfsUtil.pathToUrl(PathUtil.getJarPathForClass(SchemeFile.class));
    VirtualFile sdkFile = VirtualFileManager.getInstance().findFileByUrl(url);
    if (sdkFile != null)
    {
      VirtualFile jarFile = JarFileSystem.getInstance().getJarRootForLocalFile(sdkFile);
      if (jarFile != null)
      {
        return resolveFrom(place, getR5RSFile(jarFile));
      }
      else if (sdkFile instanceof VirtualDirectoryImpl)
      {
        return resolveFrom(place, getR5RSFile(sdkFile));
      }
    }

    return ResolveResult.CONTINUE;
  }

  private ResolveResult resolveFrom(SchemeIdentifier place, SchemeFile schemeFile)
  {
    for (SchemeList item : getSchemeCompleteItems(schemeFile))
    {
      SchemeIdentifier identifier = item.findFirstChildByClass(SchemeIdentifier.class);
      if (place.couldReference(identifier))
      {
        return ResolveResult.of(identifier);
      }
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

    String url = VfsUtil.pathToUrl(PathUtil.getJarPathForClass(SchemeFile.class));
    VirtualFile sdkFile = VirtualFileManager.getInstance().findFileByUrl(url);
    if (sdkFile != null)
    {
      VirtualFile jarFile = JarFileSystem.getInstance().getJarRootForLocalFile(sdkFile);
      if (jarFile != null)
      {
        ret.addAll(getCompletionItems(getR5RSFile(jarFile)));
      }
      else if (sdkFile instanceof VirtualDirectoryImpl)
      {
        ret.addAll(getCompletionItems(getR5RSFile(sdkFile)));
      }
    }

    return ret;
  }

  private Collection<PsiElement> getCompletionItems(SchemeFile schemeFile)
  {
    Collection<PsiElement> ret = new ArrayList<PsiElement>();
    for (SchemeList item : getSchemeCompleteItems(schemeFile))
    {
      SchemeIdentifier identifier = item.findFirstChildByClass(SchemeIdentifier.class);
      if (identifier != null)
      {
        ret.add(identifier);
      }
    }
    return ret;
  }

  private Collection<SchemeList> getSchemeCompleteItems(SchemeFile schemeFile)
  {
    Collection<SchemeList> ret = new ArrayList<SchemeList>();

    PsiElement child = schemeFile.getFirstChild();
    while ((child != null) && SchemePsiElementBase.isWrongElement(child))
    {
      child = child.getNextSibling();
    }
    if (child instanceof SchemeQuoted)
    {
      SchemeQuoted quoted = (SchemeQuoted) child;

      SchemeList items = quoted.findFirstChildByClass(SchemeList.class);
      SchemeList item = items.findFirstChildByClass(SchemeList.class);
      while (item != null)
      {
        ret.add(item);
        item = item.findFirstSiblingByClass(SchemeList.class);
      }
    }
    return ret;
  }

  private SchemeFile getR5RSFile(VirtualFile virtualFile)
  {
    VirtualFile r5rsFile = virtualFile.findFileByRelativePath("r5rs.scm");
    if (r5rsFile != null)
    {
      PsiFile file = PsiManager.getInstance(getProject()).findFile(r5rsFile);
      if (file != null)
      {
        if (file instanceof SchemeFile)
        {
          return (SchemeFile) file;
        }
      }
    }
    return null;
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
