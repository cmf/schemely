package org.jetbrains.plugins.scheme.compiler;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.file.SchemeFileType;

/**
 * @author Colin Fleming
 */
public class CompilerProjectComponent implements ProjectComponent
{
  private final Project myProject;

  public CompilerProjectComponent(Project myProject)
  {
    this.myProject = myProject;
  }

  @Override
  public void projectOpened()
  {
    CompilerManager compilerManager = CompilerManager.getInstance(myProject);
    compilerManager.addCompilableFileType(SchemeFileType.SCHEME_FILE_TYPE);

    for (SchemeCompiler compiler : CompilerManager.getInstance(myProject).getCompilers(SchemeCompiler.class))
    {
      CompilerManager.getInstance(myProject).removeCompiler(compiler);
    }
    CompilerManager.getInstance(myProject).addCompiler(new SchemeCompiler(myProject));
  }

  @Override
  public void projectClosed()
  {
  }

  @NotNull
  @Override
  public String getComponentName()
  {
    return "CompilerProjectComponent";
  }

  @Override
  public void initComponent()
  {
  }

  @Override
  public void disposeComponent()
  {
  }
}
