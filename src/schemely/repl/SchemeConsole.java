package schemely.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import schemely.file.SchemeFileType;
import schemely.psi.util.SchemePsiUtil;
import schemely.scheme.REPL;
import schemely.utils.Editors;

public class SchemeConsole extends LanguageConsoleImpl
{
  private final ConsoleHistoryModel historyModel;

  public SchemeConsole(Project project, String title, ConsoleHistoryModel historyModel)
  {
    super(project, title, SchemeFileType.SCHEME_LANGUAGE);
    this.historyModel = historyModel;
  }

  public boolean executeCurrent(boolean immediately)
  {
    REPL repl = getConsoleEditor().getUserData(REPL.REPL_KEY);
    if (repl == null)
    {
      return false;
    }

    Project project = getProject();

    Editor editor = getCurrentEditor();
    Document document = editor.getDocument();
    CaretModel caretModel = editor.getCaretModel();
    int offset = caretModel.getOffset();
    String text = document.getText();

    if (!immediately && !"".equals(text.substring(offset).trim()))
    {
      return false;
    }

    String candidate = text.trim();

    if ((SchemePsiUtil.isValidSchemeExpression(candidate, project)) || ("".equals(candidate)))
    {
      ConsoleHistoryModel consoleHistoryModel = getHistoryModel();

      TextRange range = new TextRange(0, document.getTextLength());
      editor.getSelectionModel().setSelection(range.getStartOffset(), range.getEndOffset());
      if (!StringUtil.isEmptyOrSpaces(candidate))
      {
        addCurrentToHistory(range, false, false);
        consoleHistoryModel.addToHistory(candidate);
        Editors.scrollDown(getHistoryViewer());
      }
      setInputText("");

      repl.execute(candidate);

      Editors.scrollDown(editor);
      return true;
    }

    return false;
  }

  public ConsoleHistoryModel getHistoryModel()
  {
    return this.historyModel;
  }
}
