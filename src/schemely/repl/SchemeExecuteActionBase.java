package schemely.repl;

import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.EmptyAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.IconLoader;

public abstract class SchemeExecuteActionBase extends DumbAwareAction
{
  public static final String ACTIONS_EXECUTE_ICON = "/actions/execute.png";
  protected final SchemeConsole languageConsole;
  protected final ProcessHandler processHandler;
  protected final SchemeConsoleExecuteActionHandler executeActionHandler;

  protected SchemeExecuteActionBase(SchemeConsole languageConsole,
                                    ProcessHandler processHandler,
                                    SchemeConsoleExecuteActionHandler executeActionHandler,
                                    String actionId)
  {
    super(null, null, IconLoader.getIcon(ACTIONS_EXECUTE_ICON));
    this.languageConsole = languageConsole;
    this.processHandler = processHandler;
    this.executeActionHandler = executeActionHandler;
    EmptyAction.setupAction(this, actionId, null);
  }

  @Override
  public void update(AnActionEvent e)
  {
    EditorEx editor = languageConsole.getConsoleEditor();
    Lookup lookup = LookupManager.getActiveLookup(editor);
    e.getPresentation()
      .setEnabled((!processHandler.isProcessTerminated()) && ((lookup == null) || (!lookup.isCompletion())));
  }

  public SchemeConsoleExecuteActionHandler getExecuteActionHandler()
  {
    return executeActionHandler;
  }
}
