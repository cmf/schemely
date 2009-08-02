package org.jetbrains.plugins.scheme.config;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.facet.FacetTypeRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class SchemeFacetLoader implements ApplicationComponent
{
  public static final String PLUGIN_MODULE_ID = "PLUGIN_MODULE";


  public static SchemeFacetLoader getInstance()
  {
    return ApplicationManager.getApplication().getComponent(SchemeFacetLoader.class);
  }

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
