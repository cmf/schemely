package schemely.repl.toolwindow.actions;

import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import schemely.psi.util.SchemePsiUtil;
import schemely.repl.SchemeConsole;
import schemely.repl.actions.NewSchemeConsoleAction;
import schemely.scheme.REPL;
import schemely.utils.Editors;

/**
 * @author Colin Fleming
 */
public class REPLEnterAction extends EditorWriteActionHandler implements DumbAware
{
  private final EditorActionHandler originalHandler;

  public REPLEnterAction(EditorActionHandler originalHandler)
  {
    this.originalHandler = originalHandler;
  }

  @Override
  public void executeWriteAction(Editor editor, DataContext dataContext)
  {
    REPL repl = editor.getUserData(NewSchemeConsoleAction.REPL_KEY);
    if (repl == null)
    {
      originalHandler.execute(editor, dataContext);
    }
    else if (!runExecuteAction(repl, false))
    {
      originalHandler.execute(editor, dataContext);
    }
  }

  @Override
  public boolean isEnabled(Editor editor, DataContext dataContext)
  {
    return originalHandler.isEnabled(editor, dataContext);
  }

  protected boolean runExecuteAction(REPL repl, boolean executeImmediately)
  {
    SchemeConsole console = repl.getConsoleView().getConsole();
    if (executeImmediately)
    {
      execute(repl);
      return true;
    }

    Project project = console.getProject();

    Editor editor = console.getCurrentEditor();
    Document document = editor.getDocument();
    CaretModel caretModel = editor.getCaretModel();
    int offset = caretModel.getOffset();
    String text = document.getText();

    if (!"".equals(text.substring(offset).trim()))
    {
      return false;
    }

    String candidate = text.trim();

    if ((SchemePsiUtil.isValidSchemeExpression(candidate, project)) || ("".equals(candidate)))
    {
      execute(repl);
      Editors.scrollDown(editor);
      return true;
    }

    return false;
  }

  private void execute(REPL repl)
  {
    // TODO check scrolling, unify REPL execution in single place
    SchemeConsole console = repl.getConsoleView().getConsole();
    ConsoleHistoryModel consoleHistoryModel = console.getHistoryModel();

    Document document = console.getCurrentEditor().getDocument();
    String text = document.getText();
    TextRange range = new TextRange(0, document.getTextLength());

    console.getCurrentEditor().getSelectionModel().setSelection(range.getStartOffset(), range.getEndOffset());
    if (!StringUtil.isEmptyOrSpaces(text))
    {
      console.addCurrentToHistory(range, false, false);
      consoleHistoryModel.addToHistory(text);
    }
    console.setInputText("");

    repl.execute(text);
  }
}
