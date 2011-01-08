package org.jetbrains.plugins.scheme.repl.actions;

import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.NotNullFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.SchemeBundle;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;
import org.jetbrains.plugins.scheme.repl.SchemeConsole;
import org.jetbrains.plugins.scheme.repl.SchemeConsoleExecuteActionHandler;
import org.jetbrains.plugins.scheme.repl.SchemeConsoleProcessHandler;

public abstract class SchemeConsoleActionBase extends AnAction
{
  private static final Logger LOG =
    Logger.getInstance("#org.jetbrains.plugins.scheme.repl.actions.LoadSchemeFileInConsoleAction");

  protected static SchemeConsoleProcessHandler findRunningSchemeConsole(Project project)
  {
    ProcessHandler handler = ExecutionHelper.findRunningConsole(project, new SchemeConsoleMatcher());
    if ((handler instanceof SchemeConsoleProcessHandler))
    {
      return (SchemeConsoleProcessHandler) handler;
    }
    return null;
  }

  protected static void executeCommand(Project project, String command)
  {
    SchemeConsoleProcessHandler processHandler = findRunningSchemeConsole(project);

    LOG.assertTrue(processHandler != null);

    LanguageConsoleImpl languageConsole = processHandler.getLanguageConsole();
    languageConsole.setInputText(command);

    Editor editor = languageConsole.getCurrentEditor();
    CaretModel caretModel = editor.getCaretModel();
    caretModel.moveToOffset(command.length());

    LOG.assertTrue(languageConsole instanceof SchemeConsole);

    SchemeConsole console = (SchemeConsole) languageConsole;
    SchemeConsoleExecuteActionHandler handler = console.getExecuteHandler();

    handler.runExecuteAction(console, true);
  }

  public void update(AnActionEvent e)
  {
    Presentation presentation = e.getPresentation();

    Editor editor = (Editor) e.getData(DataKeys.EDITOR);

    if (editor == null)
    {
      presentation.setEnabled(false);
      return;
    }
    Project project = editor.getProject();
    if (project == null)
    {
      presentation.setEnabled(false);
      return;
    }

    Document document = editor.getDocument();
    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if ((psiFile == null) || (!(psiFile instanceof SchemeFile)))
    {
      presentation.setEnabled(false);
      return;
    }

    VirtualFile virtualFile = psiFile.getVirtualFile();
    if ((virtualFile == null) || ((virtualFile instanceof LightVirtualFile)))
    {
      presentation.setEnabled(false);
      return;
    }
    String filePath = virtualFile.getPath();
    if (filePath == null)
    {
      presentation.setEnabled(false);
      return;
    }

    SchemeConsoleProcessHandler handler = findRunningSchemeConsole(project);
    if (handler == null)
    {
      presentation.setEnabled(false);
      return;
    }

    LanguageConsoleImpl console = handler.getLanguageConsole();
    if (!(console instanceof SchemeConsole))
    {
      presentation.setEnabled(false);
      return;
    }

    presentation.setEnabled(true);
  }

  protected static void showError(String msg)
  {
    Messages.showErrorDialog(msg, SchemeBundle.message("scheme.repl.actions.load.text.title", new Object[0]));
  }

  private static class SchemeConsoleMatcher implements NotNullFunction<String, Boolean>
  {
    @NotNull
    public Boolean fun(String cmdLine)
    {
      return Boolean.valueOf((cmdLine != null) && (cmdLine.contains("kawa.repl")));
    }
  }
}
