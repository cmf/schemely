package schemely.scheme.sisc;

import schemely.scheme.Scheme;

/**
 * @author Colin Fleming
 */
public class SISCScheme implements Scheme
{
  private final REPL repl = new SISCREPL();

  @Override
  public REPL getRepl()
  {
    return repl;
  }
}
