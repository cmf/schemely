package schemely.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.impl.source.codeStyle.HelperFactory;
import com.intellij.psi.impl.source.codeStyle.IndentHelper;
import schemely.file.SchemeFileType;
import schemely.psi.util.SchemePsiUtil;
import schemely.utils.Editors;

import java.io.IOException;
import java.io.OutputStream;

public class SchemeConsoleExecuteActionHandler
{
  private static final String SPACES = "                             ";

  private final ProcessHandler processHandler;
  private final Project project;
  private final IndentHelper indentHelper;
  private final boolean preserveMarkup;

  public SchemeConsoleExecuteActionHandler(ProcessHandler processHandler, Project project, boolean preserveMarkup)
  {
    this.processHandler = processHandler;
    this.project = project;
    this.preserveMarkup = preserveMarkup;
    indentHelper = HelperFactory.createHelper(SchemeFileType.SCHEME_FILE_TYPE, this.project);
  }

  public void processLine(String line)
  {
    OutputStream outputStream = processHandler.getProcessInput();
    try
    {
      byte[] bytes = (line + '\n').getBytes();
      outputStream.write(bytes);
      outputStream.flush();
    }
    catch (IOException ignore)
    {
    }
  }

  public void runExecuteAction(final SchemeConsole console, boolean executeImmediately)
  {
    ConsoleHistoryModel consoleHistoryModel = console.getHistoryModel();
    if (executeImmediately)
    {
      execute(console, consoleHistoryModel);
      return;
    }

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
      execute(console, consoleHistoryModel);
      Editors.scrollDown(editor);
    }
    else
    {
      console.setInputText(text + '\n' + SPACES.substring(0, console.getPrompt().length()));
    }
  }

  private void execute(LanguageConsoleImpl languageConsole, ConsoleHistoryModel consoleHistoryModel)
  {
    Document document = languageConsole.getCurrentEditor().getDocument();
    String text = document.getText();
    TextRange range = new TextRange(0, document.getTextLength());

    languageConsole.getCurrentEditor().getSelectionModel().setSelection(range.getStartOffset(), range.getEndOffset());
    languageConsole.addCurrentToHistory(range, false, preserveMarkup);
    languageConsole.setInputText("");
    if (!StringUtil.isEmptyOrSpaces(text))
    {
      consoleHistoryModel.addToHistory(text);
    }

    processLine(text);
  }
}
