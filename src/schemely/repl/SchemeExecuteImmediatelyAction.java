package schemely.repl;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class SchemeExecuteImmediatelyAction extends SchemeExecuteActionBase
{
  public SchemeExecuteImmediatelyAction(SchemeConsole languageConsole,
                                         ProcessHandler processHandler,
                                         SchemeConsoleExecuteActionHandler consoleExecuteActionHandler)
  {
    super(languageConsole, processHandler, consoleExecuteActionHandler, "Scheme.Console.Execute.Immediately");
  }

  public void actionPerformed(AnActionEvent e)
  {
    getExecuteActionHandler().runExecuteAction(this.myLanguageConsole, true);
  }
}
