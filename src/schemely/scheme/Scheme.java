package schemely.scheme;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import schemely.parser.SchemePsiCreator;
import schemely.repl.SchemeConsoleView;

/**
 * @author Colin Fleming
 */
public interface Scheme
{
  // Parsing customisations
  Lexer getLexer();

  PsiParser getParser();

  SchemePsiCreator getPsiCreator();

  boolean supportsSquareBracesForLists();

  boolean supportsInProcessREPL();

  REPL getNewInProcessREPL(Project project, SchemeConsoleView consoleView);
}
