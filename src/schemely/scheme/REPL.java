package schemely.scheme;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.repl.SchemeConsoleView;

import java.util.Collection;

/**
* @author Colin Fleming
*/
public interface REPL
{
  void execute(String command);

  void start() throws REPLException;

  void stop() throws REPLException;

  boolean isActive();

  SchemeConsoleView getConsoleView();

  AnAction[] getToolbarActions() throws REPLException;

  Collection<PsiElement> getSymbolVariants(PsiManager manager, SchemeIdentifier symbol);
}
