package org.jetbrains.plugins.scheme.config;

import com.intellij.facet.impl.ui.FacetTypeFrameworkSupportProvider;
import com.intellij.facet.ui.libraries.LibraryInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.SchemeBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ilyas
 */
public class SchemeFacetSupportProvider extends FacetTypeFrameworkSupportProvider<SchemeFacet>
{
  private static final
  Logger
    LOG =
    Logger.getInstance("#org.jetbrains.plugins.scheme.config.SchemeFacetSupportProvider");

  protected SchemeFacetSupportProvider()
  {
    super(SchemeFacetType.INSTANCE);
  }

  @NotNull
  @NonNls
  public String getLibraryName(String name)
  {
    return "scheme-" + name;
  }

  @NonNls
  public String getTitle()
  {
    return SchemeBundle.message("scheme.facet.title");
  }

  @NotNull
  public String[] getVersions()
  {
    List<String> versions = new ArrayList<String>();
    for (SchemeVersion version : SchemeVersion.values())
    {
      versions.add(version.toString());
    }
    return versions.toArray(new String[versions.size()]);
  }

  private static SchemeVersion getVersion(String versionName)
  {
    for (SchemeVersion version : SchemeVersion.values())
    {
      if (versionName.equals(version.toString()))
      {
        return version;
      }
    }
    LOG.error("invalid Scheme version: " + versionName);
    return null;
  }

  @NotNull
  protected LibraryInfo[] getLibraries(String selectedVersion)
  {
    SchemeVersion version = getVersion(selectedVersion);
    LOG.assertTrue(version != null);
    return version.getJars();
  }


  protected void setupConfiguration(SchemeFacet facet, ModifiableRootModel rootModel, String v)
  {
    //do nothing
  }

}