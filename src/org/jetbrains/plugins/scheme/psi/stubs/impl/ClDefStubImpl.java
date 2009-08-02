package org.jetbrains.plugins.scheme.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.jetbrains.plugins.scheme.psi.api.defs.ClDef;
import org.jetbrains.plugins.scheme.psi.stubs.api.ClDefStub;

/**
 * @author ilyas
 */
public class ClDefStubImpl extends StubBase<ClDef> implements ClDefStub
{
  private final StringRef myName;

  public ClDefStubImpl(StubElement parent, StringRef name, final IStubElementType elementType)
  {
    super(parent, elementType);
    myName = name;
  }

  public String getName()
  {
    return StringRef.toString(myName);
  }

}