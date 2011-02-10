package schemely.settings;

import com.intellij.openapi.project.Project;
import schemely.scheme.SchemeImplementation;

import javax.swing.*;

public class SchemeProjectSettingsForm
{
  private final SchemeProjectSettings settings;

  private JPanel panel;
  private JComboBox schemeImplementationComboBox;

  public SchemeProjectSettingsForm(Project project)
  {
    settings = SchemeProjectSettings.getInstance(project);
  }

  JComponent getComponent()
  {
    return panel;
  }

  public SchemeImplementation getSchemeImplementation()
  {
    return (SchemeImplementation) schemeImplementationComboBox.getSelectedItem();
  }

  boolean isModified()
  {
    return !schemeImplementationComboBox.getSelectedItem().equals(settings.schemeImplementation);
  }

  void reset()
  {
    schemeImplementationComboBox.setSelectedItem(settings.schemeImplementation);
  }

  private void createUIComponents()
  {
    schemeImplementationComboBox = new JComboBox(SchemeImplementation.values());
  }
}
