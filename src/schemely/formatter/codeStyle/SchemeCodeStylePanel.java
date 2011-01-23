package schemely.formatter.codeStyle;

import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import schemely.file.SchemeFileType;
import schemely.highlighter.SchemeEditorHighlighter;

import javax.swing.*;


public class SchemeCodeStylePanel extends CodeStyleAbstractPanel
{
  private JPanel myPanel;
  private JPanel myPreviewPanel;
  private JTextArea defineFormsTextArea;
  private JTabbedPane myTabbedPane;
  private JPanel myAlignPanel;

  protected SchemeCodeStylePanel(CodeStyleSettings settings)
  {
    super(settings);
    SchemeCodeStyleSettings css = settings.getCustomSettings(SchemeCodeStyleSettings.class);
    setSettings(css);
    installPreviewPanel(myPreviewPanel);
  }

  protected EditorHighlighter createHighlighter(EditorColorsScheme scheme)
  {
    return new SchemeEditorHighlighter(scheme);
  }

  protected int getRightMargin()
  {
    return 0;
  }

  protected void prepareForReformat(PsiFile psiFile)
  {
  }

  @NotNull
  protected FileType getFileType()
  {
    return SchemeFileType.SCHEME_FILE_TYPE;
  }

  protected String getPreviewText()
  {
    return "(print \"type = \" (or type \"!!YIKES!NO TYPE!!!\") \"$%$% \"\n" +
           "  (if (= \"\"\n" +
           "    text) \"!!NO TEXT!!!\" text))";
  }

  public void apply(CodeStyleSettings settings)
  {
    SchemeCodeStyleSettings scmSettings = settings.getCustomSettings(SchemeCodeStyleSettings.class);
    scmSettings.defineForms = defineFormsTextArea.getText();
    updatePreview();
  }

  public boolean isModified(CodeStyleSettings settings)
  {
    SchemeCodeStyleSettings scmSettings = settings.getCustomSettings(SchemeCodeStyleSettings.class);
    return !defineFormsTextArea.getText().equals(scmSettings.defineForms);
  }

  public JComponent getPanel()
  {
    return myPanel;
  }

  protected void resetImpl(CodeStyleSettings settings)
  {
    SchemeCodeStyleSettings scmSettings = settings.getCustomSettings(SchemeCodeStyleSettings.class);
    setSettings(scmSettings);
    updatePreview();
  }

  private void setSettings(SchemeCodeStyleSettings settings)
  {
    defineFormsTextArea.setText(settings.defineForms);
    //todo add more
  }
}
