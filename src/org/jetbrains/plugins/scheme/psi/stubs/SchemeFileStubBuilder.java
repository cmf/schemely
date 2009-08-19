package org.jetbrains.plugins.scheme.psi.stubs;

import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;
import org.jetbrains.plugins.scheme.psi.stubs.impl.SchemeFileStubImpl;

/**
 * @author ilyas
 */
public class SchemeFileStubBuilder extends DefaultStubBuilder
{
  protected StubElement createStubForFile(PsiFile file)
  {
    if (file instanceof SchemeFile && false)
    {
      return new SchemeFileStubImpl((SchemeFile) file);
    }

    return super.createStubForFile(file);
  }
}