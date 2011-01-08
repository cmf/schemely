package org.jetbrains.plugins.scheme.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.scheme.file.SchemeFileType;

public class SchemeConsole extends LanguageConsoleImpl
{
  private final ConsoleHistoryModel myHistoryModel;
  private SchemeConsoleExecuteActionHandler myExecuteHandler;

  public SchemeConsole(Project project,
                        String title,
                        ConsoleHistoryModel historyModel,
                        SchemeConsoleExecuteActionHandler handler)
  {
    super(project, title, SchemeFileType.SCHEME_LANGUAGE);
    this.myHistoryModel = historyModel;
  }

  public ConsoleHistoryModel getHistoryModel()
  {
    return this.myHistoryModel;
  }

  public SchemeConsoleExecuteActionHandler getExecuteHandler()
  {
    return this.myExecuteHandler;
  }

  public void setExecuteHandler(SchemeConsoleExecuteActionHandler handler)
  {
    this.myExecuteHandler = handler;
  }
}
