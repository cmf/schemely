package schemely.settings;

import com.intellij.openapi.project.Project;
import schemely.scheme.SchemeImplementation;

import javax.swing.*;

public class SchemeProjectSettingsForm
{
  private final SchemeProjectSettings mySettings;

  private JPanel myPanel;
  private JComboBox schemeImplementationComboBox;

  public SchemeProjectSettingsForm(Project project)
  {
    mySettings = SchemeProjectSettings.getInstance(project);
  }

  JComponent getComponent()
  {
    return myPanel;
  }

  public SchemeImplementation getSchemeImplementation()
  {
    return (SchemeImplementation) schemeImplementationComboBox.getSelectedItem();
  }

  boolean isModified()
  {
    return !schemeImplementationComboBox.getSelectedItem().equals(mySettings.schemeImplementation);
  }

  void reset()
  {
    schemeImplementationComboBox.setSelectedItem(mySettings.schemeImplementation);
  }

  private void createUIComponents()
  {
    schemeImplementationComboBox = new JComboBox(SchemeImplementation.values());
  }
}
