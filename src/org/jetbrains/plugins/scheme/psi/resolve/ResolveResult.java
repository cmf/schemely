package org.jetbrains.plugins.scheme.psi.resolve;

import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;

/**
 * @author Colin Fleming
 */
public class ResolveResult
{
  private final SchemeIdentifier result;
  private final boolean done;

  private ResolveResult(SchemeIdentifier result, boolean done)
  {
    this.result = result;
    this.done = done;
  }

  public static ResolveResult NONE = new ResolveResult(null, true);
  public static ResolveResult CONTINUE = new ResolveResult(null, false);

  public static ResolveResult of(SchemeIdentifier identifier)
  {
    return new ResolveResult(identifier, true);
  }

  public SchemeIdentifier getResult()
  {
    return result;
  }

  public boolean isDone()
  {
    return done;
  }

  @Override
  public String toString()
  {
    if (this == NONE)
    {
      return "NONE";
    }
    else if (this == CONTINUE)
    {
      return "CONTINUE";
    }
    else
    {
      return "ReturnResult: " + result;
    }
  }
}
