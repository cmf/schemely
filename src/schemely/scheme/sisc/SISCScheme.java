package schemely.scheme.sisc;

import com.intellij.openapi.project.Project;
import schemely.parser.DefaultPsiCreator;
import schemely.parser.SchemePsiCreator;
import schemely.repl.SchemeConsoleView;
import schemely.scheme.REPL;
import schemely.scheme.Scheme;
import schemely.scheme.sisc.psi.SISCPsiCreator;

/**
 * @author Colin Fleming
 */
public class SISCScheme implements Scheme
{
  @Override
  public SchemePsiCreator getPsiCreator()
  {
    return new SISCPsiCreator(new DefaultPsiCreator());
  }

  @Override
  public boolean supportsSquareBracesForLists()
  {
    return true;
  }

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
