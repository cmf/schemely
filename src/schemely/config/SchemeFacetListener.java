package schemely.config;

import com.intellij.facet.FacetManagerAdapter;
import com.intellij.facet.FacetManager;
import com.intellij.facet.Facet;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;


public class SchemeFacetListener extends FacetManagerAdapter implements ModuleComponent
{
  private MessageBusConnection connection;

  private Module module;

  public SchemeFacetListener(Module module)
  {
    this.module = module;
  }

  public void initComponent()
  {
    connection = module.getMessageBus().connect();
    connection.subscribe(FacetManager.FACETS_TOPIC, new FacetManagerAdapter()
    {
      public void facetAdded(@NotNull Facet facet)
      {
      }

      public void facetRemoved(@NotNull Facet facet)
      {
        if (facet.getTypeId() == SchemeFacet.ID)
        {
          //todo do something
        }
      }
    });
  }

  public void disposeComponent()
  {
    connection.disconnect();
  }

  @NotNull
  public String getComponentName()
  {
    return "SchemeFacetListener";
  }

  public void projectOpened()
  {
    // called when project is opened
  }

  public void projectClosed()
  {
    // called when project is being closed
  }

  public void moduleAdded()
  {
    // Invoked when the module corresponding to this component instance has been completely
    // loaded and added to the project.
  }
}
