package org.jetbrains.plugins.scheme.config;

import com.intellij.facet.ui.FacetBasedFrameworkSupportProvider;
import com.intellij.ide.util.frameworkSupport.FrameworkVersion;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.scheme.SchemeBundle;


public class SchemeFacetSupportProvider extends FacetBasedFrameworkSupportProvider<SchemeFacet>
{

  protected SchemeFacetSupportProvider()
  {
    super(SchemeFacetType.INSTANCE);
  }

  @NonNls
  public String getTitle()
  {
    return SchemeBundle.message("scheme.facet.title");
  }


  @Override
  protected void setupConfiguration(SchemeFacet schemeFacet,
                                    ModifiableRootModel modifiableRootModel,
                                    FrameworkVersion frameworkVersion)
  {
    throw new UnsupportedOperationException();
  }
}