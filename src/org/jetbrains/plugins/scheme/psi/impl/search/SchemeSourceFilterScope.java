package org.jetbrains.plugins.scheme.psi.impl.search;

import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class SchemeSourceFilterScope extends GlobalSearchScope
{
  private final GlobalSearchScope myDelegate;
  private final ProjectFileIndex myIndex;

  public SchemeSourceFilterScope(GlobalSearchScope delegate, Project project)
  {
    myDelegate = delegate;
    myIndex = ProjectRootManager.getInstance(project).getFileIndex();
  }

  public boolean contains(VirtualFile file)
  {
    if (myDelegate != null && !myDelegate.contains(file))
    {
      return false;
    }

    return myIndex.isInSourceContent(file) || myIndex.isInLibraryClasses(file);
  }

  public int compare(VirtualFile file1, VirtualFile file2)
  {
    return myDelegate != null ? myDelegate.compare(file1, file2) : 0;
  }

  public boolean isSearchInModuleContent(@NotNull Module aModule)
  {
    return myDelegate == null || myDelegate.isSearchInModuleContent(aModule);
  }

  public boolean isSearchInLibraries()
  {
    return myDelegate == null || myDelegate.isSearchInLibraries();
  }
}
