package schemely.scheme.sisc;

import com.intellij.openapi.project.Project;
import schemely.repl.SchemeConsoleView;
import schemely.scheme.Scheme;

/**
 * @author Colin Fleming
 */
public class SISCScheme implements Scheme
{
  @Override
  public boolean supportsInProcessREPL()
  {
    return true;
  }

  @Override
  public REPL getNewInProcessREPL(Project project, SchemeConsoleView consoleView)
  {
    return new SISCInProcessREPL(project, consoleView);
  }
}
