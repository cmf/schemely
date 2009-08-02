package org.jetbrains.plugins.scheme.psi.stubs.elements;

import org.jetbrains.plugins.scheme.psi.stubs.api.ClDefStub;
import org.jetbrains.plugins.scheme.psi.stubs.index.ClDefNameIndex;
import org.jetbrains.plugins.scheme.psi.stubs.impl.ClDefStubImpl;
import org.jetbrains.plugins.scheme.psi.api.defs.ClDef;
import org.jetbrains.plugins.scheme.psi.ClStubElementType;
import org.jetbrains.plugins.scheme.psi.impl.defs.ClDefnMethodImpl;
import org.jetbrains.plugins.scheme.parser.AST;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.util.io.StringRef;

import java.io.IOException;

/**
 * @author ilyas
 */
public class ClDefMethodElementType extends ClStubElementType<ClDefStub, ClDef>
{
  public ClDefMethodElementType()
  {
    super("defmethod");
  }

  public void serialize(ClDefStub stub, StubOutputStream dataStream) throws IOException
  {
    dataStream.writeName(stub.getName());
  }

  public ClDefStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException
  {
    StringRef ref = dataStream.readName();
    return new ClDefStubImpl(parentStub, ref, this);
  }

  public ClDef createPsi(ClDefStub stub)
  {
    return new ClDefnMethodImpl(stub, AST.DEFMETHOD);
  }

  public ClDefStub createStub(ClDef psi, StubElement parentStub)
  {
    return new ClDefStubImpl(parentStub, StringRef.fromString(psi.getName()), AST.DEFMETHOD);
  }

  @Override
  public void indexStub(ClDefStub stub, IndexSink sink)
  {
    final String name = stub.getName();
    if (name != null)
    {
      sink.occurrence(ClDefNameIndex.KEY, name);
    }
  }
}
