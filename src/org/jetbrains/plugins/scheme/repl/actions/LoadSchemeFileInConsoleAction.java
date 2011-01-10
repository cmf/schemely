package org.jetbrains.plugins.scheme.repl.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.scheme.SchemeIcons;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;

public class LoadSchemeFileInConsoleAction extends SchemeConsoleActionBase
{
  public LoadSchemeFileInConsoleAction()
  {
    getTemplatePresentation().setIcon(SchemeIcons.SCHEME_ICON);
  }

  public void update(AnActionEvent e)
  {
    super.update(e);
  }

  public void actionPerformed(AnActionEvent e)
  {
    Editor editor = e.getData(DataKeys.EDITOR);

    if (editor == null)
    {
      return;
    }
    Project project = editor.getProject();
    if (project == null)
    {
      return;
    }

    Document document = editor.getDocument();
    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if ((psiFile == null) || (!(psiFile instanceof SchemeFile)))
    {
      return;
    }

    VirtualFile virtualFile = psiFile.getVirtualFile();
    if (virtualFile == null)
    {
      return;
    }
    String filePath = virtualFile.getPath();
    if (filePath == null)
    {
      return;
    }

    String command = "(load \"" + filePath + "\")";

    PsiDocumentManager.getInstance(project).commitAllDocuments();
    FileDocumentManager.getInstance().saveAllDocuments();

    executeCommand(project, command);
  }
}
