package org.jetbrains.plugins.scheme.config;

import com.intellij.facet.FacetManagerAdapter;
import com.intellij.facet.FacetManager;
import com.intellij.facet.Facet;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;


public class SchemeFacetListener extends FacetManagerAdapter implements ModuleComponent
{
  private MessageBusConnection myConnection;

  private Module myModule;

  public SchemeFacetListener(Module module)
  {
    myModule = module;
  }

  public void initComponent()
  {
    myConnection = myModule.getMessageBus().connect();
    myConnection.subscribe(FacetManager.FACETS_TOPIC, new FacetManagerAdapter()
    {
      public void facetAdded(@NotNull Facet facet)
      {
      }

      public void facetRemoved(@NotNull Facet facet)
      {
        if (facet.getTypeId() == SchemeFacet.ID)
        {
          //todo do somethig
        }
      }
    });
  }

  public void disposeComponent()
  {
    myConnection.disconnect();
  }

  @NotNull
  public String getComponentName()
  {
    return "SchemeFacetListener";
  }

  public void projectOpened()
  {
    // called when myProject is opened
  }

  public void projectClosed()
  {
    // called when myProject is being closed
  }

  public void moduleAdded()
  {
    // Invoked when the module corresponding to this component instance has been completely
    // loaded and added to the myProject.
  }
}
