package schemely.repl;

import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.project.Project;

public class SchemeConsoleView extends LanguageConsoleViewImpl
{
  public SchemeConsoleView(Project project, String title, ConsoleHistoryModel historyModel)
  {
    super(project, new SchemeConsole(project, title, historyModel));
  }

  public SchemeConsole getConsole()
  {
    return (SchemeConsole) super.getConsole();
  }
}
