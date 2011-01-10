package org.jetbrains.plugins.scheme.repl.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.scheme.SchemeIcons;

public class RunSelectedTextAction extends SchemeConsoleActionBase
{
  public RunSelectedTextAction()
  {
    getTemplatePresentation().setIcon(SchemeIcons.SCHEME_ICON);
  }

  public void actionPerformed(AnActionEvent e)
  {
    Editor editor = e.getData(DataKeys.EDITOR);
    if (editor == null)
    {
      return;
    }
    SelectionModel selectionModel = editor.getSelectionModel();
    String selectedText = selectionModel.getSelectedText();
    if ((selectedText == null) || (selectedText.trim().length() == 0))
    {
      return;
    }
    String text = selectedText.trim();
    Project project = editor.getProject();

//    TODO
//    String msg = SchemePsiElementFactory.getInstance(project).getErrorMessage(text);
//    if (msg != null)
//    {
//      Messages.showErrorDialog(project,
//                               SchemeBundle.message("evaluate.incorrect.form", new Object[]{msg}),
//                               SchemeBundle.message("evaluate.incorrect.cannot.evaluate", new Object[0]));
//
//      return;
//    }

    executeCommand(project, text);
  }
}
