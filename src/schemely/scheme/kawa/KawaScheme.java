package schemely.scheme.kawa;

import com.intellij.openapi.project.Project;
import schemely.repl.SchemeConsoleView;
import schemely.scheme.Scheme;

/**
 * @author Colin Fleming
 */
public class KawaScheme implements Scheme
{
  @Override
  public boolean supportsSquareBracesForLists()
  {
    return false;
  }

  @Override
  public boolean supportsInProcessREPL()
  {
    return false;
  }

  @Override
  public REPL getNewInProcessREPL(Project project, SchemeConsoleView consoleView)
  {
    throw new UnsupportedOperationException("Kawa does not support in-process REPL");
  }
}
