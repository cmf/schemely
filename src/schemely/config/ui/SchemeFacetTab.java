
package schemely.config.ui;

import com.intellij.facet.Facet;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import schemely.SchemeBundle;
import schemely.config.SchemeLibrariesConfiguration;

import javax.swing.*;


public class SchemeFacetTab extends FacetEditorTab
{
  private JPanel panel;
  private JCheckBox compilerExcludeCb;
  private JCheckBox libraryExcludeCb;
  private final SchemeLibrariesConfiguration configuration;

  public SchemeFacetTab(FacetEditorContext editorContext, SchemeLibrariesConfiguration configuration)
  {

    this.configuration = configuration;

    compilerExcludeCb.setSelected(this.configuration.excludeCompilerFromModuleScope);
    libraryExcludeCb.setSelected(this.configuration.excludeSdkFromModuleScope);

//    compilerExcludeCb.setVisible(false);
//    libraryExcludeCb.setVisible(false);
    reset();
  }

  @Nls
  public String getDisplayName()
  {
    return SchemeBundle.message("scheme.sdk.configuration");
  }

  public JComponent createComponent()
  {
    return panel;
  }

  public boolean isModified()
  {
    return !(configuration.excludeCompilerFromModuleScope == compilerExcludeCb.isSelected() &&
             configuration.excludeSdkFromModuleScope == libraryExcludeCb.isSelected());
  }

  @Override
  public String getHelpTopic()
  {
    return super.getHelpTopic();
  }

  public void onFacetInitialized(@NotNull Facet facet)
  {
  }

  public void apply() throws ConfigurationException
  {
    configuration.excludeCompilerFromModuleScope = compilerExcludeCb.isSelected();
    configuration.excludeSdkFromModuleScope = libraryExcludeCb.isSelected();
  }

  public void reset()
  {
  }

  public void disposeUIResources()
  {
  }

  private void createUIComponents()
  {
  }
}
