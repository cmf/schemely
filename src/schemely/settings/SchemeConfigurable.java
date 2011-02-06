package schemely.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import schemely.SchemeIcons;
import schemely.scheme.SchemeImplementation;

import javax.swing.*;

public class SchemeConfigurable extends AbstractProjectComponent implements Configurable
{
  protected static final String PROJECT_SETTINGS = "SchemeProjectSettings";
  private SchemeProjectSettingsForm mySettingsForm;
  private SchemeImplementation originalImplementation;

  private volatile Runnable myReloadProjectRequest;

  public SchemeConfigurable(Project project)
  {
    super(project);
    originalImplementation = SchemeProjectSettings.getInstance(myProject).schemeImplementation;
  }

  // Configurable =============================================================

  @Override
  @Nls
  public String getDisplayName()
  {
    return "Scheme";
  }

  @Override
  public Icon getIcon()
  {
    return SchemeIcons.SCHEME_ICON;
  }

  @Override
  public String getHelpTopic()
  {
    return null;
  }

  @Override
  public JComponent createComponent()
  {
    if (mySettingsForm == null)
    {
      mySettingsForm = new SchemeProjectSettingsForm(myProject);
    }
    return mySettingsForm.getComponent();
  }

  @Override
  public boolean isModified()
  {
    return mySettingsForm.isModified();
  }

  @Override
  public void apply() throws ConfigurationException
  {
    SchemeProjectSettings settings = SchemeProjectSettings.getInstance(myProject);
    SchemeImplementation implementation = mySettingsForm.getSchemeImplementation();
    settings.schemeImplementation = implementation;
    reloadProjectOnLanguageLevelChange(implementation, false);
  }

  @Override
  public void reset()
  {
    if (mySettingsForm != null)
    {
      mySettingsForm.reset();
    }
  }

  @Override
  public void disposeUIResources()
  {
    mySettingsForm = null;
  }

  // ProjectComponent =========================================================

  @Override
  @NotNull
  public String getComponentName()
  {
    return PROJECT_SETTINGS;
  }

  public void reloadProjectOnLanguageLevelChange(@NotNull SchemeImplementation implementation, final boolean forceReload)
  {
    if (willReload())
    {
      myReloadProjectRequest = new Runnable()
      {
        @Override
        public void run()
        {
          if (myProject.isDisposed())
          {
            return;
          }
          if (myReloadProjectRequest != this)
          {
            // obsolete, another request has already replaced this one
            return;
          }
          SchemeImplementation currentImplementation = SchemeProjectSettings.getInstance(myProject).schemeImplementation;
          if (!forceReload && originalImplementation.equals(currentImplementation)) {
            // the question does not make sense now
            return;
          }
          String message =
            "Scheme implementation changes will take effect on project reload.\nWould you like to reload project \"" +
            myProject.getName() +
            "\" now?";
          if (Messages.showYesNoDialog(myProject,
                                       message,
                                       "Implementation changed",
                                       Messages.getQuestionIcon()) == 0)
          {
            ProjectManager.getInstance().reloadProject(myProject);
          }
          myReloadProjectRequest = null;
        }
      };
      ApplicationManager.getApplication().invokeLater(myReloadProjectRequest, ModalityState.NON_MODAL);
    }
    else
    {
      // if the project is not open, reset the original implementation to the same value as implementation
      originalImplementation = implementation;
    }
  }

  private boolean willReload()
  {
    return myProject.isOpen() && !ApplicationManager.getApplication().isUnitTestMode();
  }
}