package schemely.compiler;

import com.intellij.compiler.CompilerException;
import com.intellij.compiler.impl.javaCompiler.BackendCompiler;
import com.intellij.compiler.impl.javaCompiler.BackendCompilerWrapper;
import com.intellij.compiler.make.CacheCorruptedException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.compiler.ex.CompileContextEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Chunk;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import schemely.file.SchemeFileType;
import schemely.psi.impl.SchemeFile;

import java.util.Arrays;

/**
 * @author Colin Fleming
 */
public class SchemeCompiler implements TranslatingCompiler
{
  private static final Logger log = Logger.getLogger(SchemeCompiler.class);

  private static final FileTypeManager FILE_TYPE_MANAGER = FileTypeManager.getInstance();
  private final Project myProject;

  public SchemeCompiler(Project myProject)
  {
    this.myProject = myProject;
  }

  @Override
  public boolean isCompilableFile(final VirtualFile virtualFile, CompileContext compileContext)
  {
    FileType fileType = FILE_TYPE_MANAGER.getFileTypeByFile(virtualFile);
    PsiFile psi = ApplicationManager.getApplication().runReadAction(new Computable<PsiFile>()
    {
      public PsiFile compute()
      {
        return PsiManager.getInstance(myProject).findFile(virtualFile);
      }
    });

    return fileType.equals(SchemeFileType.SCHEME_FILE_TYPE) && (psi instanceof SchemeFile);
  }

  @Override
  public void compile(CompileContext compileContext,
                      Chunk<Module> moduleChunk,
                      VirtualFile[] virtualFiles,
                      OutputSink outputSink)
  {
    BackendCompiler backEndCompiler = getBackEndCompiler();
    BackendCompilerWrapper
      wrapper =
      new BackendCompilerWrapper(moduleChunk,
                                 myProject,
                                 Arrays.asList(virtualFiles),
                                 (CompileContextEx) compileContext,
                                 backEndCompiler,
                                 outputSink);

    // Compile Scheme classes
    try
    {
      wrapper.compile();
    }
    catch (CompilerException e)
    {
      compileContext.addMessage(CompilerMessageCategory.ERROR, e.getMessage(), null, -1, -1);
    }
    catch (CacheCorruptedException e)
    {
      log.info(e);
      compileContext.requestRebuildNextTime(e.getMessage());
    }
  }

  @NotNull
  @Override
  public String getDescription()
  {
    return "Scheme Compiler";
  }

  public boolean validateConfiguration(CompileScope scope)
  {
    return getBackEndCompiler().checkCompiler(scope);
  }

  private BackendCompiler getBackEndCompiler()
  {
    return new SchemeBackendCompiler(myProject);
  }
}
