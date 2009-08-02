package org.jetbrains.plugins.scheme.structure;

import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.psi.api.defs.ClDef;

/**
 * @author ilyas
 */
public class SchemeStructureViewModel extends TextEditorBasedStructureViewModel
{
  private PsiFile myFile;

  public SchemeStructureViewModel(final PsiFile file)
  {
    super(file);
    myFile = file;
  }

  @NotNull
  public StructureViewTreeElement getRoot()
  {
    return new SchemeStructureViewElement(myFile);
  }

  @NotNull
  public Grouper[] getGroupers()
  {
    return Grouper.EMPTY_ARRAY;
  }

  @NotNull
  public Sorter[] getSorters()
  {
    return new Sorter[]{Sorter.ALPHA_SORTER};
  }

  @NotNull
  public Filter[] getFilters()
  {
    return Filter.EMPTY_ARRAY;
  }

  protected PsiFile getPsiFile()
  {
    return myFile;
  }

  @NotNull
  protected Class[] getSuitableClasses()
  {
    return new Class[]{ClDef.class};
  }
}
