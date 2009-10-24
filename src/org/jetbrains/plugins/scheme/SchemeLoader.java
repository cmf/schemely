package org.jetbrains.plugins.scheme;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import org.jetbrains.annotations.NotNull;

public class SchemeLoader implements ApplicationComponent
{
  public SchemeLoader()
  {
  }

  public void initComponent()
  {
    loadScheme();
  }

  private void loadScheme()
  {
    ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerAdapter()
    {
      public void projectOpened(Project project)
      {
      }
    });

  }

  public void disposeComponent()
  {
  }

  @NotNull
  public String getComponentName()
  {
    return "scheme.support.loader";
  }

}
