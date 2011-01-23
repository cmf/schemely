package schemely.psi.stubs.impl;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import schemely.parser.AST;
import schemely.psi.impl.SchemeFile;
import schemely.psi.stubs.api.SchemeFileStub;


public class SchemeFileStubImpl extends PsiFileStubImpl<SchemeFile> implements SchemeFileStub
{
  private final StringRef myPackageName;
  private final StringRef myName;
  private final boolean isClassDefinition;

  public SchemeFileStubImpl(SchemeFile file)
  {
    super(file);
    myPackageName = StringRef.fromString(file.getPackageName());
    isClassDefinition = false;
    myName = StringRef.fromString(null);
  }

  public SchemeFileStubImpl(StringRef packName, StringRef name, boolean isScript)
  {
    super(null);
    myPackageName = packName;
    myName = name;
    this.isClassDefinition = isScript;
  }

  public IStubFileElementType getType()
  {
    return AST.FILE;
  }

  public StringRef getPackageName()
  {
    return myPackageName;
  }

  public StringRef getName()
  {
    return myName;
  }

  public boolean isClassDefinition()
  {
    return isClassDefinition;
  }
}