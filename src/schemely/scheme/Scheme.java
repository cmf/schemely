package schemely.scheme;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.repl.SchemeConsoleView;

import java.util.Collection;

/**
 * @author Colin Fleming
 */
public interface Scheme
{
  boolean supportsInProcessREPL();

  REPL getNewInProcessREPL(Project project, SchemeConsoleView consoleView);

//  InProcessREPLHandler getInProcessReplHandler();
//
//  ProcessREPLHandler getProcessReplHandler();
//
//  interface InProcessREPLHandler
//  {
//  }
//
//  interface ProcessREPLHandler
//  {
//    List<String> createRuntimeArgs(Module module, String workingDir) throws CantRunException;
//
//    void processOutput(LanguageConsoleImpl console, String text, Key attributes);
//
//    NotNullFunction<String, Boolean> getConsoleMatcher();
//  }

  interface REPL
  {
    void execute(String command);

    void start() throws REPLException;

    void stop() throws REPLException;

    void clear() throws REPLException;

    boolean isActive();

    SchemeConsoleView getConsoleView();

    AnAction[] getToolbarActions() throws REPLException;

    Collection<PsiElement> getSymbolVariants(PsiManager manager, SchemeIdentifier symbol);
  }
}
