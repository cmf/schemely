package org.jetbrains.plugins.scheme.psi.stubs.elements;

import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;

import java.io.IOException;

import org.jetbrains.plugins.scheme.psi.stubs.SchemeFileStubBuilder;
import org.jetbrains.plugins.scheme.psi.stubs.index.SchemeClassNameIndex;
import org.jetbrains.plugins.scheme.psi.stubs.index.SchemeFullScriptNameIndex;
import org.jetbrains.plugins.scheme.psi.stubs.impl.ClFileStubImpl;
import org.jetbrains.plugins.scheme.psi.stubs.api.ClFileStub;
import org.jetbrains.plugins.scheme.file.SchemeFileType;

/**
 * @author ilyas
 */
public class ClStubFileElementType extends IStubFileElementType<ClFileStub>
{
  private static final int CACHES_VERSION = 10;

  public ClStubFileElementType()
  {
    super(SchemeFileType.SCHEME_LANGUAGE);
  }

  public StubBuilder getBuilder()
  {
    return new SchemeFileStubBuilder();
  }

  @Override
  public int getStubVersion()
  {
    return super.getStubVersion() + CACHES_VERSION;
  }

  public String getExternalId()
  {
    return "scheme.FILE";
  }

  @Override
  public void indexStub(PsiFileStub stub, IndexSink sink)
  {
    super.indexStub(stub, sink);
  }

  @Override
  public void serialize(final ClFileStub stub, final StubOutputStream dataStream) throws IOException
  {
    dataStream.writeName(stub.getPackageName().toString());
    dataStream.writeName(stub.getName().toString());
    dataStream.writeBoolean(stub.isClassDefinition());
  }

  @Override
  public ClFileStub deserialize(final StubInputStream dataStream, final StubElement parentStub) throws IOException
  {
    StringRef packName = dataStream.readName();
    StringRef name = dataStream.readName();
    boolean isScript = dataStream.readBoolean();
    return new ClFileStubImpl(packName, name, isScript);
  }

  public void indexStub(ClFileStub stub, IndexSink sink)
  {
    String name = stub.getName().toString();
    if (stub.isClassDefinition() && name != null)
    {
      sink.occurrence(SchemeClassNameIndex.KEY, name);
      final String pName = stub.getPackageName().toString();
      final String fqn = pName == null || pName.length() == 0 ? name : pName + "." + name;
      sink.occurrence(SchemeFullScriptNameIndex.KEY, fqn.hashCode());
    }
  }

}