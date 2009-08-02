package org.jetbrains.plugins.scheme.psi.stubs;

import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;
import org.jetbrains.plugins.scheme.psi.stubs.impl.ClFileStubImpl;

/**
 * @author ilyas
 */
public class SchemeFileStubBuilder extends DefaultStubBuilder
{
  protected StubElement createStubForFile(final PsiFile file)
  {
    if (file instanceof SchemeFile && ((SchemeFile) file).isClassDefiningFile())
    {
      return new ClFileStubImpl((SchemeFile) file);
    }

    return super.createStubForFile(file);
  }
}