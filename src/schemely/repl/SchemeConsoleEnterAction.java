package schemely.repl;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class SchemeConsoleEnterAction extends SchemeExecuteActionBase
{
  public SchemeConsoleEnterAction(SchemeConsole languageConsole,
                                  ProcessHandler processHandler,
                                  SchemeConsoleExecuteActionHandler executeActionHandler)
  {
    super(languageConsole, processHandler, executeActionHandler, SchemeConsoleRunner.EXECUTE_ACTION_ID);
  }

  @Override
  public void actionPerformed(AnActionEvent e)
  {
    getExecuteActionHandler().runExecuteAction(languageConsole, false);
  }
}
