package schemely.repl.toolwindow.actions;

import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.impl.source.codeStyle.HelperFactory;
import com.intellij.psi.impl.source.codeStyle.IndentHelper;
import schemely.file.SchemeFileType;
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
  protected IndentHelper indentHelper = null;

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
    else
    {
      runExecuteAction(repl, false);
    }
  }

  protected void runExecuteAction(REPL repl, boolean executeImmediately)
  {
    final SchemeConsole console = repl.getConsoleView().getConsole();
    if (executeImmediately)
    {
      execute(repl);
      return;
    }

    Project project = console.getProject();
    indentHelper = HelperFactory.createHelper(SchemeFileType.SCHEME_FILE_TYPE, project);

    Editor editor = console.getCurrentEditor();
    Document document = editor.getDocument();
    final CaretModel caretModel = editor.getCaretModel();
    final int offset = caretModel.getOffset();
    String text = document.getText();

    if (!"".equals(text.substring(offset).trim()))
    {
      String before = text.substring(0, offset);
      String after = text.substring(offset);
      final int indent = indentHelper.getIndent(before, false);
      String spaces = indentHelper.fillIndent(indent);
      final String newText = before + '\n' + spaces + after;

      new WriteCommandAction(project)
      {
        @Override
        protected void run(Result result) throws Throwable
        {
          console.setInputText(newText);
          caretModel.moveToOffset(offset + indent + 1);
        }
      }.execute();

      return;
    }

    String candidate = text.trim();

    if ((SchemePsiUtil.isValidSchemeExpression(candidate, project)) || ("".equals(candidate)))
    {
      execute(repl);
      Editors.scrollDown(editor);
    }
    else
    {
      int indent = indentHelper.getIndent(text, false);
      String spaces = indentHelper.fillIndent(indent);
      String newText = text + '\n' + spaces + spaces(console.getPrompt().length());
      console.setInputText(newText);
    }
  }

  private String spaces(int length)
  {
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++)
    {
      builder.append(' ');
    }

    return builder.toString();
  }

  private void execute(REPL repl)
  {
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
