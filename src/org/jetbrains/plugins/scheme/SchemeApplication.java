package org.jetbrains.plugins.scheme;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.file.SchemeFileType;

public class SchemeApplication implements ApplicationComponent
{
  public void initComponent()
  {
    ApplicationManager.getApplication().runWriteAction(new Runnable()
    {
      public void run()
      {
        FileTypeManager.getInstance().registerFileType(new SchemeFileType(), "ss", "sch");
      }
    });
  }

  public void disposeComponent()
  {
    // TODO: insert component disposal logic here
  }

  @NotNull
  public String getComponentName()
  {
    return "SchemeApplication";
  }
}
