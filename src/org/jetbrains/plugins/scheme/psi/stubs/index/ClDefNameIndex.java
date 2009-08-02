package org.jetbrains.plugins.scheme.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.plugins.scheme.psi.api.defs.ClDef;

/**
 * @author ilyas
 */
public class ClDefNameIndex extends StringStubIndexExtension<ClDef>
{
  public static final StubIndexKey<String, ClDef> KEY = StubIndexKey.createIndexKey("scm.def.name");

  public StubIndexKey<String, ClDef> getKey()
  {
    return KEY;
  }
}