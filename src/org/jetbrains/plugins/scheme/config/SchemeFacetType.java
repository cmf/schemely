package org.jetbrains.plugins.scheme.config;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetModel;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.facet.autodetecting.DetectedFacetPresentation;
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.facet.autodetecting.FacetDetectorRegistry;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.SchemeBundle;
import org.jetbrains.plugins.scheme.SchemeIcons;
import org.jetbrains.plugins.scheme.file.SchemeFileType;

import javax.swing.*;
import java.util.Collection;


public class SchemeFacetType extends FacetType<SchemeFacet, SchemeFacetConfiguration>
{
  public static final SchemeFacetType INSTANCE = new SchemeFacetType();

  private SchemeFacetType()
  {
    super(SchemeFacet.ID, "Scheme", "Scheme");
  }

  public SchemeFacetConfiguration createDefaultConfiguration()
  {
    return new SchemeFacetConfiguration();
  }

  public SchemeFacet createFacet(@NotNull Module module,
                                 String name,
                                 @NotNull SchemeFacetConfiguration configuration,
                                 @Nullable Facet underlyingFacet)
  {
    return new SchemeFacet(this, module, name, configuration, underlyingFacet);
  }

  public Icon getIcon()
  {
    return SchemeIcons.SCHEME_ICON;
  }

  public boolean isSuitableModuleType(ModuleType moduleType)
  {
    return (moduleType instanceof JavaModuleType || "PLUGIN_MODULE".equals(moduleType.getId()));
  }

  public void registerDetectors(final FacetDetectorRegistry<SchemeFacetConfiguration> registry)
  {
    FacetDetector<VirtualFile, SchemeFacetConfiguration> detector = new SchemeFacetDetector();

    final Ref<Boolean> alreadyDetected = new Ref<Boolean>(false);
    VirtualFileFilter filter = new VirtualFileFilter()
    {
      public boolean accept(VirtualFile virtualFile)
      {
        if (alreadyDetected.get())
        {
          return true;
        }
        alreadyDetected.set(true);
        if (SchemeFileType.SCHEME_EXTENSIONS.equals(virtualFile.getExtension()))
        {
          registry.customizeDetectedFacetPresentation(new SchemeFacetPresentation());
          return true;
        }

        return false;
      }
    };

    registry.registerUniversalDetector(SchemeFileType.SCHEME_FILE_TYPE, filter, detector);
  }

  public static SchemeFacetType getInstance()
  {
    SchemeFacetType facetType = (SchemeFacetType) FacetTypeRegistry.getInstance().findFacetType(SchemeFacet.ID);
    assert facetType != null;
    return facetType;
  }

  private class SchemeFacetDetector extends FacetDetector<VirtualFile, SchemeFacetConfiguration>
  {
    public SchemeFacetDetector()
    {
      super("scheme-detector");
    }

    public SchemeFacetConfiguration detectFacet(VirtualFile source,
                                                Collection<SchemeFacetConfiguration> existentFacetConfigurations)
    {
      if (!existentFacetConfigurations.isEmpty())
      {
        return existentFacetConfigurations.iterator().next();
      }
      return createDefaultConfiguration();
    }

    public void beforeFacetAdded(@NotNull Facet facet, FacetModel facetModel, @NotNull ModifiableRootModel model)
    {
    }
  }

  private static class SchemeFacetPresentation extends DetectedFacetPresentation
  {
    @Override
    public String getAutodetectionPopupText(@NotNull Module module,
                                            @NotNull FacetType facetType,
                                            @NotNull String facetName,
                                            @NotNull VirtualFile[] files)
    {
      return SchemeBundle.message("new.scheme.facet.detected");
    }

  }

}