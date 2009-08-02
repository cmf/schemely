package org.jetbrains.plugins.scheme.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.impl.source.PsiFileWithStubSupport;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.file.SchemeFileType;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.ClList;
import org.jetbrains.plugins.scheme.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.scheme.psi.impl.synthetic.ClSyntheticClassImpl;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.scheme.psi.util.SchemePsiUtil;
import org.jetbrains.plugins.scheme.psi.util.SchemeTextUtil;

/**
 * User: peter
 * Date: Nov 21, 2008
 * Time: 9:50:00 AM
 * Copyright 2007, 2008, 2009 Red Shark Technology
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SchemeFile extends PsiFileBase implements SchemePsiElement, PsiFile, PsiFileWithStubSupport, PsiElement
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
        myClass = new ClSyntheticClassImpl(this);
      }

      myScriptClassInitialized = true;
    }
    return myClass;
  }


  protected PsiFileImpl clone()
  {
    final SchemeFile clone = (SchemeFile) super.clone();
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

  private boolean isWrongElement(PsiElement element)
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
    return (T) element;
  }

  public PsiElement getSecondNonLeafElement()
  {
    return null;
  }

  public void setContext(PsiElement context)
  {
    if (context != null)
    {
      myContext = context;
    }
  }

  public boolean isClassDefiningFile()
  {
    final ClList ns = SchemePsiUtil.findFormByName(this, "ns");
    if (ns == null)
    {
      return false;
    }
    final ClSymbol first = ns.findFirstChildByClass(ClSymbol.class);
    if (first == null)
    {
      return false;
    }
    final ClSymbol snd = SchemePsiUtil.findNextSiblingByClass(first, ClSymbol.class);
    if (snd == null)
    {
      return false;
    }

    return SchemePsiUtil.findNamespaceKeyByName(ns, SchemePsiUtil.GEN_CLASS) != null;
  }

  public String getNamespace()
  {
    final ClList ns = getNamespaceElement();
    if (ns == null)
    {
      return null;
    }
    final ClSymbol first = ns.findFirstChildByClass(ClSymbol.class);
    if (first == null)
    {
      return null;
    }
    final ClSymbol snd = SchemePsiUtil.findNextSiblingByClass(first, ClSymbol.class);
    if (snd == null)
    {
      return null;
    }

    return snd.getNameString();
  }

  public ClList getNamespaceElement()
  {
    // TODO CMF
    return null; //SchemePsiUtil.findFormByNameSet(this, SchemeParser.NS_TOKENS);
  }

  public String getClassName()
  {
    String namespace = getNamespace();
    if (namespace == null)
    {
      return null;
    }
    int i = namespace.lastIndexOf(".");
    return i > 0 && i < namespace.length() - 1 ? namespace.substring(i + 1) : namespace;
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place)
  {
    //Process precedent read forms
    ResolveUtil.processChildren(this, processor, state, lastParent, place);

    final JavaPsiFacade facade = JavaPsiFacade.getInstance(getProject());

    // Add all java.lang classes
    final PsiPackage javaLang = facade.findPackage(SchemePsiUtil.JAVA_LANG);
    if (javaLang != null)
    {
      for (PsiClass clazz : javaLang.getClasses())
      {
        if (!ResolveUtil.processElement(processor, clazz))
        {
          return false;
        }
      }
    }

    //Add top-level package names
    final PsiPackage rootPackage = JavaPsiFacade.getInstance(getProject()).findPackage("");
    if (rootPackage != null)
    {
      rootPackage.processDeclarations(processor, state, null, place);
    }

    return super.processDeclarations(processor, state, lastParent, place);
  }

  public PsiElement setClassName(@NonNls String s)
  {
    //todo implement me!
    return null;
  }
}
