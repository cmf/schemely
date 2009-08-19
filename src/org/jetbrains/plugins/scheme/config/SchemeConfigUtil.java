package org.jetbrains.plugins.scheme.config;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.utils.LibrariesUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author ilyas
 */
public class SchemeConfigUtil
{
  public static final String SCHEME_JAR_NAME_PREFIX = "scheme";

  public static final String LIBRARY_PROPERTIES_PATH = "library.properties";

  public static final String SCHEME_MAIN_CLASS_FILE = "scheme/main.class";

  public static final String VERSION_PROPERTY_KEY = "version.number";

  public static final String UNDEFINED_VERSION = "undefined";

  private static final Condition<Library> SCHEME_LIB_CONDITION = new Condition<Library>()
  {
    public boolean value(Library library)
    {
      return isSchemeLibrary(library);
    }
  };

  /**
   * Checks wheter given IDEA library contains Scheme Library classes
   */
  public static boolean isSchemeLibrary(Library library)
  {
    return library != null && checkLibrary(library, SCHEME_JAR_NAME_PREFIX, SCHEME_MAIN_CLASS_FILE);
  }

  static boolean checkLibrary(Library library, String jarNamePrefix, String necessaryClass)
  {
    boolean result = false;
    VirtualFile[] classFiles = library.getFiles(OrderRootType.CLASSES);
    for (VirtualFile file : classFiles)
    {
      String path = file.getPath();
      if (path != null && "jar".equals(file.getExtension()))
      {
        path = StringUtil.trimEnd(path, "!/");
        String name = file.getName();

        File realFile = new File(path);
        if (realFile.exists())
        {
          try
          {
            JarFile jarFile = new JarFile(realFile);
            if (name.startsWith(jarNamePrefix))
            {
              result = jarFile.getJarEntry(necessaryClass) != null;
            }
            jarFile.close();
          }
          catch (IOException e)
          {
            result = false;
          }
        }
      }
    }
    return result;
  }

  private static String getSchemeVersion(@NotNull String jarPath)
  {
    String jarVersion = getSchemeJarVersion(jarPath, LIBRARY_PROPERTIES_PATH);
    return jarVersion != null ? jarVersion : UNDEFINED_VERSION;
  }

  /**
   * Return value of Implementation-Version attribute in jar manifest
   * <p/>
   *
   * @param jarPath  path to jar file
   * @param propPath path to properties file in jar file
   * @return value of Implementation-Version attribute, null if not found
   */
  public static String getSchemeJarVersion(String jarPath, String propPath)
  {
    try
    {
      File file = new File(jarPath);
      if (!file.exists())
      {
        return null;
      }
      JarFile jarFile = new JarFile(file);
      JarEntry jarEntry = jarFile.getJarEntry(propPath);
      if (jarEntry == null)
      {
        return null;
      }
      Properties properties = new Properties();
      properties.load(jarFile.getInputStream(jarEntry));
      String version = properties.getProperty(VERSION_PROPERTY_KEY);
      jarFile.close();
      return version;
    }
    catch (Exception e)
    {
      return null;
    }
  }

  public static Library[] getProjectSchemeLibraries(Project project)
  {
    if (project == null)
    {
      return new Library[0];
    }
    LibraryTable table = ProjectLibraryTable.getInstance(project);
    List<Library> all = ContainerUtil.findAll(table.getLibraries(), SCHEME_LIB_CONDITION);
    return all.toArray(new Library[all.size()]);
  }

  public static Library[] getAllSchemeLibraries(@Nullable Project project)
  {
    return ArrayUtil.mergeArrays(getGlobalSchemeLibraries(), getProjectSchemeLibraries(project), Library.class);
  }

  public static Library[] getGlobalSchemeLibraries()
  {
    return LibrariesUtil.getGlobalLibraries(SCHEME_LIB_CONDITION);
  }

  static String getSpecificJarForLibrary(Library library, String jarNamePrefix, String necessaryClass)
  {
    VirtualFile[] classFiles = library.getFiles(OrderRootType.CLASSES);
    for (VirtualFile file : classFiles)
    {
      String path = file.getPath();
      if (path != null && "jar".equals(file.getExtension()))
      {
        path = StringUtil.trimEnd(path, "!/");
        String name = file.getName();

        File realFile = new File(path);
        if (realFile.exists())
        {
          try
          {
            JarFile jarFile = new JarFile(realFile);
            if (name.startsWith(jarNamePrefix) && jarFile.getJarEntry(necessaryClass) != null)
            {
              return path;
            }
            jarFile.close();
          }
          catch (IOException e)
          {
            //do nothing
          }
        }
      }
    }
    return "";
  }

  public static Library[] getSchemeSdkLibrariesByModule(Module module)
  {
    return LibrariesUtil.getLibrariesByCondition(module, SCHEME_LIB_CONDITION);
  }

  @NotNull
  public static String getSchemeSdkJarPath(Module module)
  {
    if (module == null)
    {
      return "";
    }
    Library[] libraries = getSchemeSdkLibrariesByModule(module);
    if (libraries.length == 0)
    {
      return "";
    }
    Library library = libraries[0];
    return getSchemeJarPathForLibrary(library);
  }

  public static String getSchemeJarPathForLibrary(Library library)
  {
    return getSpecificJarForLibrary(library, SCHEME_JAR_NAME_PREFIX, SCHEME_MAIN_CLASS_FILE);
  }


  public static boolean isSchemeConfigured(Module module)
  {
    ModuleRootManager manager = ModuleRootManager.getInstance(module);
    for (OrderEntry entry : manager.getOrderEntries())
    {
      if (entry instanceof LibraryOrderEntry)
      {
        Library library = ((LibraryOrderEntry) entry).getLibrary();
        if (library != null)
        {
          for (VirtualFile file : library.getFiles(OrderRootType.CLASSES))
          {
            String path = file.getPath();
            if (path.endsWith(".jar!/"))
            {
              if (file.findFileByRelativePath(SCHEME_MAIN_CLASS_FILE) != null)
              {
                return true;
              }
            }
          }
        }
      }
    }
    return false;

  }
}
