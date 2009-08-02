package org.jetbrains.plugins.scheme.psi;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.file.SchemeFileType;

/**
 * @author ilyas
 */
public abstract class ClStubElementType<S extends StubElement, T extends SchemePsiElement> extends IStubElementType<S, T>
{
  public ClStubElementType(@NonNls @NotNull String debugName)
  {
    super(debugName, SchemeFileType.SCHEME_LANGUAGE);
  }

  public void indexStub(final S stub, final IndexSink sink)
  {
  }

  public String getExternalId()
  {
    return "scm." + super.toString();
  }

}
