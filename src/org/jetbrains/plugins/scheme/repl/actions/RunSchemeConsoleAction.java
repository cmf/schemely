package org.jetbrains.plugins.scheme.repl.actions;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.scheme.SchemeBundle;
import org.jetbrains.plugins.scheme.SchemeIcons;
import org.jetbrains.plugins.scheme.config.SchemeFacet;
import org.jetbrains.plugins.scheme.config.SchemeFacetType;
import org.jetbrains.plugins.scheme.repl.SchemeConsoleRunner;

import java.util.Arrays;

public class RunSchemeConsoleAction extends AnAction implements DumbAware
{
  public RunSchemeConsoleAction()
  {
    getTemplatePresentation().setIcon(SchemeIcons.SCHEME_ICON);
  }

  public void update(AnActionEvent e)
  {
    Module m = getModule(e);
    Presentation presentation = e.getPresentation();
    if (m == null)
    {
      presentation.setEnabled(false);
      return;
    }
    presentation.setEnabled(true);
    super.update(e);
  }

  public void actionPerformed(AnActionEvent event)
  {
    Module module = getModule(event);
    assert (module != null) : "Module is null";
    String path = com.intellij.openapi.roots.ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();

    String title = SchemeBundle.message("repl.toolWindowName");
    try
    {
      SchemeConsoleRunner.run(module, path, new String[0]);
    }
    catch (CantRunException e)
    {
      ExecutionHelper.showErrors(module.getProject(), Arrays.<Exception>asList(e), title, null);
    }
  }

  static Module getModule(AnActionEvent e)
  {
    Module module = e.getData(DataKeys.MODULE);
    if (module == null)
    {
      Project project = e.getData(DataKeys.PROJECT);

      if (project == null)
      {
        return null;
      }
      Module[] modules = ModuleManager.getInstance(project).getModules();
      if (modules.length == 1)
      {
        module = modules[0];
      }
      else
      {
        for (Module m : modules)
        {
          FacetManager manager = FacetManager.getInstance(m);
          SchemeFacet clFacet = manager.getFacetByType(SchemeFacetType.INSTANCE.getId());
          if (clFacet != null)
          {
            module = m;
            break;
          }
        }
        if (module == null)
        {
          module = modules[0];
        }
      }
    }
    return module;
  }
}