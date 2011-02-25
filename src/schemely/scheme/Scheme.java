package schemely.scheme;

import com.intellij.openapi.project.Project;
import schemely.parser.SchemePsiCreator;
import schemely.repl.SchemeConsoleView;

/**
 * @author Colin Fleming
 */
public interface Scheme
{
  // Parsing customisations
  SchemePsiCreator getPsiCreator();

  boolean supportsSquareBracesForLists();

  boolean supportsInProcessREPL();

  REPL getNewInProcessREPL(Project project, SchemeConsoleView consoleView);
}
