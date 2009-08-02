package org.jetbrains.plugins.scheme.psi.impl.javaView;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;
import org.jetbrains.plugins.scheme.psi.impl.SchemePsiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ilyas
 */
public class SchemeClassFinder implements ProjectComponent, PsiElementFinder
{
  private final Project myProject;

  public SchemeClassFinder(Project project)
  {
    myProject = project;
  }

  @Nullable
  public PsiClass findClass(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope)
  {
    return SchemePsiManager.getInstance(myProject).getNamesCache().getClassByFQName(qualifiedName, scope);
  }

  @NotNull
  public PsiClass[] findClasses(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope)
  {
    return SchemePsiManager.getInstance(myProject).getNamesCache().getClassesByFQName(qualifiedName, scope);
  }

  @Nullable
  public PsiPackage findPackage(@NotNull String qualifiedName)
  {
    return null;
  }

  @NotNull
  public PsiPackage[] getSubPackages(@NotNull PsiPackage psiPackage, @NotNull GlobalSearchScope scope)
  {
    return new PsiPackage[0];
  }

  @NotNull
  public PsiClass[] getClasses(@NotNull PsiPackage psiPackage, @NotNull GlobalSearchScope scope)
  {
    List<PsiClass> result = new ArrayList<PsiClass>();
    for (final PsiDirectory dir : psiPackage.getDirectories(scope))
    {
      for (final PsiFile file : dir.getFiles())
      {
        if (file instanceof SchemeFile)
        {
          SchemeFile schemeFile = (SchemeFile) file;
          if (schemeFile.isClassDefiningFile() && schemeFile.getPackageName().equals(psiPackage.getQualifiedName()))
          {
            result.add(schemeFile.getDefinedClass());
          }
        }
      }
    }

    return result.toArray(new PsiClass[result.size()]);
  }

  public void projectOpened()
  {
  }

  public void projectClosed()
  {
  }

  @NonNls
  @NotNull
  public String getComponentName()
  {
    return "SchemeClassFinder";
  }

  public void initComponent()
  {
  }

  public void disposeComponent()
  {
  }
}