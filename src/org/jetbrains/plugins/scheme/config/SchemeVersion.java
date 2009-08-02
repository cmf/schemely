package org.jetbrains.plugins.scheme.config;

import com.intellij.facet.ui.libraries.LibraryInfo;
import org.jetbrains.annotations.NonNls;
import static org.jetbrains.plugins.scheme.config.util.SchemeMavenLibraryUtil.createJarDownloadInfo;


/**
 * @author ilyas
 */
public enum SchemeVersion
{
  Scheme_1_0("1.0",
             new LibraryInfo[]{createJarDownloadInfo("scheme.jar", "", "scheme.main"),
                               createJarDownloadInfo("scheme-contrib.jar", ""),});

  private final String myName;
  private final LibraryInfo[] myJars;

  private SchemeVersion(@NonNls String name, LibraryInfo[] infos)
  {
    myName = name;
    myJars = infos;
  }

  public LibraryInfo[] getJars()
  {
    return myJars;
  }

  public String toString()
  {
    return myName;
  }

}