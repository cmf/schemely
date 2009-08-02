package org.jetbrains.plugins.scheme.psi.stubs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;
import org.jetbrains.plugins.scheme.psi.stubs.index.SchemeClassNameIndex;
import org.jetbrains.plugins.scheme.psi.stubs.index.SchemeFullScriptNameIndex;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author ilyas
 */
public class SchemeShortNamesCache implements PsiShortNamesCache
{
  Project myProject;

  public SchemeShortNamesCache(Project project)
  {
    myProject = project;
  }


  public void runStartupActivity()
  {
  }

  @NotNull
  public PsiFile[] getFilesByName(@NotNull String name)
  {
    return new PsiFile[0];
  }

  @NotNull
  public String[] getAllFileNames()
  {
    return FilenameIndex.getAllFilenames();
  }

  private boolean areClassesCompiled()
  {
    return false;
  }

  @NotNull
  public PsiClass[] getClassesByName(@NotNull String name, @NotNull GlobalSearchScope scope)
  {
    if (!areClassesCompiled())
    {
      return PsiClass.EMPTY_ARRAY;
    }

    Collection<PsiClass> allClasses = getAllScriptClasses(name, scope);
    if (allClasses.isEmpty())
    {
      return PsiClass.EMPTY_ARRAY;
    }
    return allClasses.toArray(new PsiClass[allClasses.size()]);
  }

  private Collection<PsiClass> getAllScriptClasses(String name, GlobalSearchScope scope)
  {
    if (!areClassesCompiled())
    {
      return new ArrayList<PsiClass>();
    }

    Collection<SchemeFile> files = StubIndex.getInstance().get(SchemeClassNameIndex.KEY, name, myProject, scope);
    files = ContainerUtil.findAll(files, new Condition<SchemeFile>()
    {
      public boolean value(SchemeFile schemeFile)
      {
        return schemeFile.isClassDefiningFile();
      }
    });
    return ContainerUtil.map(files, new Function<SchemeFile, PsiClass>()
    {
      public PsiClass fun(SchemeFile schemeFile)
      {
        assert schemeFile.isClassDefiningFile();
        return schemeFile.getDefinedClass();
      }
    });
  }

  private Collection<PsiClass> getScriptClassesByFQName(final String name, final GlobalSearchScope scope)
  {
    Collection<SchemeFile>
      scripts =
      StubIndex.getInstance().get(SchemeFullScriptNameIndex.KEY, name.hashCode(), myProject, scope);

    scripts = ContainerUtil.findAll(scripts, new Condition<SchemeFile>()
    {
      public boolean value(final SchemeFile schemeFile)
      {
        final PsiClass clazz = schemeFile.getDefinedClass();
        return schemeFile.isClassDefiningFile() && clazz != null && name.equals(clazz.getQualifiedName());
      }
    });
    return ContainerUtil.map(scripts, new Function<SchemeFile, PsiClass>()
    {
      public PsiClass fun(final SchemeFile schemeFile)
      {
        return schemeFile.getDefinedClass();
      }
    });
  }

  @NotNull
  public String[] getAllClassNames()
  {
    if (!areClassesCompiled())
    {
      return new String[0];
    }

    final Collection<String> classNames = StubIndex.getInstance().getAllKeys(SchemeClassNameIndex.KEY);
    return classNames.toArray(new String[classNames.size()]);
  }

  public void getAllClassNames(@NotNull HashSet<String> dest)
  {
    if (!areClassesCompiled())
    {
      return;
    }

    final Collection<String> classNames = StubIndex.getInstance().getAllKeys(SchemeClassNameIndex.KEY);
    dest.addAll(classNames);
  }

  @Nullable
  public PsiClass getClassByFQName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope)
  {
    if (!areClassesCompiled())
    {
      return null;
    }

    final Collection<PsiClass> scriptClasses = getScriptClassesByFQName(name, scope);
    for (PsiClass clazz : scriptClasses)
    {
      if (name.equals(clazz.getQualifiedName()))
      {
        return clazz;
      }
    }
    return null;
  }

  @NotNull
  public PsiClass[] getClassesByFQName(@NotNull @NonNls String fqn, @NotNull GlobalSearchScope scope)
  {
    if (!areClassesCompiled())
    {
      return PsiClass.EMPTY_ARRAY;
    }

    final Collection<PsiClass> result = getScriptClassesByFQName(fqn, scope);
    ArrayList<PsiClass> filtered = new ArrayList<PsiClass>();
    for (PsiClass clazz : result)
    {
      if (fqn.equals(clazz.getQualifiedName()))
      {
        filtered.add(clazz);
      }
    }
    return filtered.isEmpty() ? PsiClass.EMPTY_ARRAY : filtered.toArray(new PsiClass[filtered.size()]);
  }


  @NotNull
  public PsiMethod[] getMethodsByName(@NonNls String name, @NotNull GlobalSearchScope scope)
  {
    return new PsiMethod[0];
  }

  @NotNull
  public PsiMethod[] getMethodsByNameIfNotMoreThan(@NonNls String name, @NotNull GlobalSearchScope scope, int maxCount)
  {
    return new PsiMethod[0];
  }

  @NotNull
  public String[] getAllMethodNames()
  {
    return new String[0];
  }

  public void getAllMethodNames(@NotNull HashSet<String> set)
  {
  }

  @NotNull
  public PsiField[] getFieldsByName(@NotNull String name, @NotNull GlobalSearchScope scope)
  {
    return new PsiField[0];
  }

  @NotNull
  public String[] getAllFieldNames()
  {
    return new String[0];
  }

  public void getAllFieldNames(@NotNull HashSet<String> set)
  {
  }

}
