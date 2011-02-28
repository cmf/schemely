package schemely.scheme.sisc.psi;

import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import schemely.psi.impl.SchemeFile;
import schemely.scheme.sisc.SISCConfigUtil;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Colin Fleming
 */
public class SISCFile extends SchemeFile
{
  private static final Collection<String> bootFiles = Arrays.asList("sisc/boot/init.scm",
                                                                    "sisc/boot/compat.scm",
                                                                    "sisc/boot/analyzer.scm",
                                                                    "sisc/boot/eval.scm",
                                                                    "sisc/boot/init2.scm",
                                                                    "sisc/boot/repl.scm",
                                                                    // TODO maybe control if we load this
                                                                    "sisc/modules/std-modules.scm");

  public SISCFile(FileViewProvider viewProvider)
  {
    super(viewProvider);
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place)
  {
    if (!processTopLevelDefinitions(processor, state, lastParent, place))
    {
      return false;
    }

    String sourcePath = SISCConfigUtil.getJarPathForResource(sisc.REPL.class, "sisc/boot/repl.scm");
    String sourceURL = VfsUtil.pathToUrl(sourcePath);
    VirtualFile sourceFile = VirtualFileManager.getInstance().findFileByUrl(sourceURL);
    if (sourceFile != null)
    {
      VirtualFile jarFile = JarFileSystem.getInstance().getJarRootForLocalFile(sourceFile);
      if (jarFile != null)
      {
        for (String bootFile : bootFiles)
        {
          VirtualFile file = jarFile.findFileByRelativePath(bootFile);
          if (file != null)
          {
            PsiFile psiFile = PsiManager.getInstance(getProject()).findFile(file);
            if (psiFile instanceof SchemeFile)
            {
              SchemeFile schemeFile = (SchemeFile) psiFile;
              if (!schemeFile.processTopLevelDefinitions(processor, state, lastParent, place))
              {
                return false;
              }
            }
          }
        }
      }
    }
    return true;
  }

}
