package org.jetbrains.plugins.scheme.repl;

import gnu.expr.Language;
import kawa.Shell;
import kawa.standard.Scheme;

/**
 * @author Colin Fleming
 */
public class KawaRepl
{
  public static void main(String[] args)
  {
    Scheme scheme = new Scheme();
    Language.setCurrentLanguage(scheme);
    Language.setDefaults(scheme);
    boolean ok = Shell.run(scheme, scheme.getEnvironment());
    if (!ok)
    {
      System.exit(-1);
    }
  }
}
