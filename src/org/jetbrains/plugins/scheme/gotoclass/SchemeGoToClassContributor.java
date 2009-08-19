package org.jetbrains.plugins.scheme.gotoclass;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;
import org.jetbrains.plugins.scheme.psi.stubs.index.SchemeClassNameIndex;

import java.util.Collection;
import java.util.List;

/**
 * @author ilyas
 */
public class SchemeGoToClassContributor implements ChooseByNameContributor
{
  public String[] getNames(Project project, boolean includeNonProjectItems)
  {
    Collection<String> classNames = StubIndex.getInstance().getAllKeys(SchemeClassNameIndex.KEY);
    return classNames.toArray(new String[classNames.size()]);
  }

  public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems)
  {
    GlobalSearchScope scope = includeNonProjectItems ? null : GlobalSearchScope.projectScope(project);
    Collection<SchemeFile> files = StubIndex.getInstance().get(SchemeClassNameIndex.KEY, name, project, scope);
    List<PsiClass> scriptClasses = ContainerUtil.map(files, new Function<SchemeFile, PsiClass>()
    {
      public PsiClass fun(SchemeFile schemeFile)
      {
        assert false;
        return schemeFile.getDefinedClass();
      }
    });
    return scriptClasses.toArray(new NavigationItem[scriptClasses.size()]);
  }

}