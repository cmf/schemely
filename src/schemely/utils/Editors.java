package schemely.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;

/**
 * @author Colin Fleming
 */
public class Editors
{
  public static void scrollDown(final Editor editor)
  {
    ApplicationManager.getApplication().invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        editor.getCaretModel().moveToOffset(editor.getDocument().getTextLength());
      }
    });
  }
}
