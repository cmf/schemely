package org.jetbrains.plugins.scheme.psi.util;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.file.SchemeFileType;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;


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

  private SchemeFile createSchemeFileFromText(String text)
  {
    return (SchemeFile) PsiFileFactory.getInstance(getProject())
      .createFileFromText(DUMMY + SchemeFileType.SCHEME_FILE_TYPE.getDefaultExtension(), text);
  }

  public Project getProject()
  {
    return myProject;
  }
}
