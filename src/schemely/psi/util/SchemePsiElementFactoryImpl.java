package schemely.psi.util;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import schemely.file.SchemeFileType;
import schemely.psi.impl.SchemeFile;


public class SchemePsiElementFactoryImpl extends SchemePsiElementFactory
{
  private final Project myProject;

  public SchemePsiElementFactoryImpl(Project project)
  {
    myProject = project;
  }

  private static final String DUMMY = "DUMMY.";


  public ASTNode createSymbolNodeFromText(@NotNull String newName)
  {
    String text = "(" + newName + ")";
    SchemeFile dummyFile = createSchemeFileFromText(text);
    return dummyFile.getFirstChild().getFirstChild().getNextSibling().getNode();
  }

  @Override
  public SchemeFile createSchemeFileFromText(String text)
  {
    return (SchemeFile) PsiFileFactory.getInstance(getProject())
      .createFileFromText(DUMMY + SchemeFileType.SCHEME_FILE_TYPE.getDefaultExtension(), text);
  }

  @Override
  public boolean hasSyntacticalErrors(@NotNull String text)
  {
    SchemeFile schemeFile = (SchemeFile) PsiFileFactory.getInstance(getProject())
      .createFileFromText(DUMMY + SchemeFileType.SCHEME_FILE_TYPE.getDefaultExtension(), text);
    return hasErrorElement(schemeFile);
  }

  private static boolean hasErrorElement(PsiElement element)
  {
    if ((element instanceof PsiErrorElement))
    {
      return true;
    }
    for (PsiElement child : element.getChildren())
    {
      if (hasErrorElement(child))
      {
        return true;
      }
    }
    return false;
  }

  public Project getProject()
  {
    return myProject;
  }
}
