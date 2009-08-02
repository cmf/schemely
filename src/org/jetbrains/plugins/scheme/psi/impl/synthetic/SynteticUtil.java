package org.jetbrains.plugins.scheme.psi.impl.synthetic;

import org.jetbrains.plugins.scheme.psi.api.defs.ClDef;

/**
 * @author ilyas
 */
public abstract class SynteticUtil
{
  public static String getJavaMethodByDef(ClDef def)
  {
    return "public static void main";
  }
}
