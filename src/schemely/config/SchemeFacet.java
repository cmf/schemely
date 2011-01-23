package schemely.config;

import com.intellij.facet.*;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;


public class SchemeFacet extends Facet<SchemeFacetConfiguration>
{
  public static final String FACET_TYPE_ID_STRING = "scheme";
  public final static FacetTypeId<SchemeFacet> ID = new FacetTypeId<SchemeFacet>(FACET_TYPE_ID_STRING);

  public SchemeFacet(@NotNull Module module)
  {
    this(FacetTypeRegistry.getInstance().findFacetType(FACET_TYPE_ID_STRING),
         module,
         "Scheme",
         new SchemeFacetConfiguration(),
         null);
  }


  public SchemeFacet(FacetType facetType,
                     Module module,
                     String name,
                     SchemeFacetConfiguration configuration,
                     Facet underlyingFacet)
  {
    super(facetType, module, name, configuration, underlyingFacet);
  }

  public static SchemeFacet getInstance(@NotNull Module module)
  {
    return FacetManager.getInstance(module).getFacetByType(ID);
  }

}
