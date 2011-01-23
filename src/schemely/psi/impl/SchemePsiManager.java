package schemely.psi.impl;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import schemely.psi.stubs.SchemeShortNamesCache;
import schemely.file.SchemeFileType;


public class SchemePsiManager implements ProjectComponent
{
  private final Project myProject;
  private SchemeShortNamesCache myCache;
  private PsiFile myDummyFile;

  public SchemePsiManager(Project project)
  {
    myProject = project;
  }

  public void projectOpened()
  {
  }

  public void projectClosed()
  {
  }

  @NotNull
  public String getComponentName()
  {
    return "SchemePsiManager";
  }

  public void initComponent()
  {
    myCache = new SchemeShortNamesCache(myProject);
    StartupManager.getInstance(myProject).registerPostStartupActivity(new Runnable()
    {
      public void run()
      {
        ApplicationManager.getApplication().runWriteAction(new Runnable()
        {
          public void run()
          {
            if (!myProject.isDisposed())
            {
              JavaPsiFacade.getInstance(myProject).registerShortNamesCache(getNamesCache());
            }
          }
        });
      }
    });

    myDummyFile =
      PsiFileFactory.getInstance(myProject)
        .createFileFromText("dummy." + SchemeFileType.SCHEME_FILE_TYPE.getDefaultExtension(), "");
  }

  public void disposeComponent()
  {
  }

  public static SchemePsiManager getInstance(Project project)
  {
    return project.getComponent(SchemePsiManager.class);
  }

  public SchemeShortNamesCache getNamesCache()
  {
    return myCache;
  }

}
