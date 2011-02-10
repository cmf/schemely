package schemely.repl;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class SchemeExecuteImmediatelyAction extends SchemeExecuteActionBase
{
  public SchemeExecuteImmediatelyAction(SchemeConsole languageConsole,
                                        ProcessHandler processHandler,
                                        SchemeConsoleExecuteActionHandler executeActionHandler)
  {
    super(languageConsole,
          processHandler,
          executeActionHandler,
          SchemeConsoleRunner.EXECUTE_ACTION_IMMEDIATELY_ID);
  }

  @Override
  public void actionPerformed(AnActionEvent e)
  {
    getExecuteActionHandler().runExecuteAction(languageConsole, true);
  }
}
