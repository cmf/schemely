package schemely.scheme.kawa;

import schemely.scheme.Scheme;

/**
 * @author Colin Fleming
 */
public class KawaScheme implements Scheme
{
  private final REPL repl = new KawaREPL();

  @Override
  public REPL getRepl()
  {
    return repl;
  }
}
