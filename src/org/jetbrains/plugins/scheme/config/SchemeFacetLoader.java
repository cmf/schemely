package org.jetbrains.plugins.scheme.config;

import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;


public class SchemeFacetLoader implements ApplicationComponent
{
  public SchemeFacetLoader()
  {
  }

  public void initComponent()
  {
    FacetTypeRegistry.getInstance().registerFacetType(SchemeFacetType.INSTANCE);
  }

  public void disposeComponent()
  {
    FacetTypeRegistry instance = FacetTypeRegistry.getInstance();
    instance.unregisterFacetType(instance.findFacetType(SchemeFacet.ID));
  }

  @NotNull
  public String getComponentName()
  {
    return "SchemeFacetLoader";
  }
}
