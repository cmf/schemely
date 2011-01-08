package org.jetbrains.plugins.scheme.repl;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class SchemeConsoleEnterAction extends SchemeExecuteActionBase
{
  public SchemeConsoleEnterAction(SchemeConsole languageConsole,
                                   ProcessHandler processHandler,
                                   SchemeConsoleExecuteActionHandler consoleExecuteActionHandler)
  {
    super(languageConsole, processHandler, consoleExecuteActionHandler, "Scheme.Console.Execute");
  }

  public void actionPerformed(AnActionEvent e)
  {
    getExecuteActionHandler().runExecuteAction(this.myLanguageConsole, false);
  }
}
