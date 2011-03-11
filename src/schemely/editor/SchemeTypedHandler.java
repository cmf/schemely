package schemely.editor;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.apache.log4j.Logger;
import schemely.psi.impl.SchemeFile;

/**
 * @author Colin Fleming
 */
public class SchemeTypedHandler extends TypedHandlerDelegate
{
  private static final Logger log = Logger.getLogger(SchemeTypedHandler.class);

  @Override
  public Result charTyped(char c, Project project, Editor editor, PsiFile file)
  {
    if (!(file instanceof SchemeFile))
    {
      return Result.CONTINUE;
    }

    Document document = editor.getDocument();
    int startOffset = editor.getCaretModel().getOffset();
    int line = editor.offsetToLogicalPosition(startOffset).line;
    int col = editor.getCaretModel().getLogicalPosition().column;
    int lineStart = document.getLineStartOffset(line);
    int initLineEnd = document.getLineEndOffset(line);

    // Check that the line is blank except for the new char
    TextRange textRange = new TextRange(lineStart, startOffset);
    String lineText = document.getText(textRange);
    if (shouldApplyTo(c, lineText))
    {
      try
      {
        PsiDocumentManager.getInstance(project).commitDocument(document);
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());

        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
        int newPos = codeStyleManager.adjustLineIndent(file, lineStart);
        int newCol = newPos - lineStart;
        int lineInc = document.getLineEndOffset(line) - initLineEnd;
        if (newCol >= col + lineInc)
        {
          LogicalPosition pos = new LogicalPosition(line, newCol);
          editor.getCaretModel().moveToLogicalPosition(pos);
          editor.getSelectionModel().removeSelection();
          editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        }
      }
      catch (IncorrectOperationException e)
      {
        log.error(e);
      }
    }

    return Result.CONTINUE;
  }

  private boolean shouldApplyTo(char c, String line)
  {
    int count = 0;
    for (int i = 0; i < line.length(); i++)
    {
      char ch = line.charAt(i);
      if (ch == c)
      {
        count++;
      }
      else if (!Character.isWhitespace(ch))
      {
        return false;
      }
    }
    return count == 1;
  }
}
