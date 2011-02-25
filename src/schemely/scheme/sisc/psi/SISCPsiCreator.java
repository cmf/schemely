package schemely.scheme.sisc.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import schemely.parser.SchemePsiCreator;

/**
 * @author Colin Fleming
 */
public class SISCPsiCreator implements SchemePsiCreator
{
  private final SchemePsiCreator original;

  public SISCPsiCreator(SchemePsiCreator original)
  {
    this.original = original;
  }

  @Override
  public PsiElement createElement(ASTNode node)
  {
    return original.createElement(node);
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider)
  {
    return new SISCFile(viewProvider);
  }
}
