package schemely.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.project.Project;
import schemely.file.SchemeFileType;

public class SchemeConsole extends LanguageConsoleImpl
{
  private final ConsoleHistoryModel historyModel;
  private SchemeConsoleExecuteActionHandler executeHandler;

  public SchemeConsole(Project project, String title, ConsoleHistoryModel historyModel)
  {
    super(project, title, SchemeFileType.SCHEME_LANGUAGE);
    this.historyModel = historyModel;
  }

  public ConsoleHistoryModel getHistoryModel()
  {
    return this.historyModel;
  }

  public SchemeConsoleExecuteActionHandler getExecuteHandler()
  {
    return this.executeHandler;
  }

  public void setExecuteHandler(SchemeConsoleExecuteActionHandler handler)
  {
    this.executeHandler = handler;
  }
}
