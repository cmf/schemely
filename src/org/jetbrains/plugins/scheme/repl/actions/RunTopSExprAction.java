package org.jetbrains.plugins.scheme.repl.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.scheme.SchemeBundle;
import org.jetbrains.plugins.scheme.SchemeIcons;
import org.jetbrains.plugins.scheme.psi.util.SchemePsiElementFactory;
import org.jetbrains.plugins.scheme.psi.util.SchemePsiUtil;

public final class RunTopSExprAction extends SchemeConsoleActionBase
{
  public RunTopSExprAction()
  {
    getTemplatePresentation().setIcon(SchemeIcons.SCHEME_ICON);
  }

  public void actionPerformed(AnActionEvent event)
  {
    Editor editor = event.getData(DataKeys.EDITOR);
    if (editor == null)
    {
      return;
    }

    Project project = editor.getProject();
    if (project == null)
    {
      return;
    }

    PsiElement sexp = SchemePsiUtil.findTopSexpAroundCaret(editor);
    if (sexp == null)
    {
      return;
    }

    String text = sexp.getText();
    if (SchemePsiElementFactory.getInstance(project).hasSyntacticalErrors(text))
    {
      Messages.showErrorDialog(project,
                               SchemeBundle.message("evaluate.incorrect.sexp"),
                               SchemeBundle.message("evaluate.incorrect.cannot.evaluate"));

      return;
    }

    executeCommand(project, text);
  }
}
