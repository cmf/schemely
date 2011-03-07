package schemely.scheme.kawa;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import schemely.lexer.SchemeLexer;
import schemely.parser.DefaultPsiCreator;
import schemely.parser.SchemeParser;
import schemely.parser.SchemePsiCreator;
import schemely.repl.SchemeConsoleView;
import schemely.scheme.REPL;
import schemely.scheme.Scheme;

/**
 * @author Colin Fleming
 */
public class KawaScheme implements Scheme
{
  @Override
  public Lexer getLexer()
  {
    return new SchemeLexer();
  }

  @Override
  public PsiParser getParser()
  {
    return new SchemeParser();
  }

  @Override
  public SchemePsiCreator getPsiCreator()
  {
    return new DefaultPsiCreator();
  }

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
