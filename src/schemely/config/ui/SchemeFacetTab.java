/*
 * Copyright 2000-2008 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
