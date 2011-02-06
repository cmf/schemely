
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
  private JPanel myPanel;
  private JCheckBox myCompilerExcludeCb;
  private JCheckBox myLibraryExcludeCb;
  private final SchemeLibrariesConfiguration myConfiguration;

  public SchemeFacetTab(FacetEditorContext editorContext, SchemeLibrariesConfiguration configuration)
  {

    myConfiguration = configuration;

    myCompilerExcludeCb.setSelected(myConfiguration.myExcludeCompilerFromModuleScope);
    myLibraryExcludeCb.setSelected(myConfiguration.myExcludeSdkFromModuleScope);

//    myCompilerExcludeCb.setVisible(false);
//    myLibraryExcludeCb.setVisible(false);
    reset();
  }

  @Nls
  public String getDisplayName()
  {
    return SchemeBundle.message("scheme.sdk.configuration");
  }

  public JComponent createComponent()
  {
    return myPanel;
  }

  public boolean isModified()
  {
    return !(myConfiguration.myExcludeCompilerFromModuleScope == myCompilerExcludeCb.isSelected() &&
             myConfiguration.myExcludeSdkFromModuleScope == myLibraryExcludeCb.isSelected());
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
    myConfiguration.myExcludeCompilerFromModuleScope = myCompilerExcludeCb.isSelected();
    myConfiguration.myExcludeSdkFromModuleScope = myLibraryExcludeCb.isSelected();
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
