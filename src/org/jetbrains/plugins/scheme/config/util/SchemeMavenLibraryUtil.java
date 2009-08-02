package org.jetbrains.plugins.scheme.config.util;

import com.intellij.facet.ui.libraries.LibraryInfo;
import org.jetbrains.annotations.NonNls;

/**
 * @author ilyas
 */
public class SchemeMavenLibraryUtil
{
  @NonNls
  private static final String DOWNLOAD_JETBRAINS_COM = "http://download.jetbrains.com";
  @NonNls
  private static final String DOWNLOADING_URL = DOWNLOAD_JETBRAINS_COM + "/idea/scheme/";

  private SchemeMavenLibraryUtil()
  {
  }

  public static LibraryInfo createJarDownloadInfo(final String jarName,
                                                  final String version,
                                                  final String... requiredClasses)
  {
    return new LibraryInfo(jarName, version, DOWNLOADING_URL + jarName, DOWNLOAD_JETBRAINS_COM, requiredClasses);
  }

}
