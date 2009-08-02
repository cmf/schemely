package org.jetbrains.plugins.scheme.formatter.codeStyle;

import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.file.SchemeFileType;
import org.jetbrains.plugins.scheme.highlighter.SchemeEditorHighlighter;

import javax.swing.*;

/**
 * @author ilyas
 */
public class SchemeCodeStylePanel extends CodeStyleAbstractPanel
{
  private final CodeStyleSettings mySettings;
  private JPanel myPanel;
  private JCheckBox alignCheckBox;
  private JTabbedPane myTabbedPane;
  private JPanel myAlignPanel;
  private JPanel myPreviewPanel;

  protected SchemeCodeStylePanel(CodeStyleSettings settings)
  {
    super(settings);
    mySettings = settings;
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
    scmSettings.ALIGN_SCHEME_FORMS = alignCheckBox.isSelected();
    updatePreview();
  }

  public boolean isModified(CodeStyleSettings settings)
  {
    SchemeCodeStyleSettings scmSettings = settings.getCustomSettings(SchemeCodeStyleSettings.class);
    if (alignCheckBox.isSelected() ^ scmSettings.ALIGN_SCHEME_FORMS)
    {
      return true;
    }
    return false;
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
    setValue(alignCheckBox, settings.ALIGN_SCHEME_FORMS);
    //todo add more
  }

  private static void setValue(final JCheckBox box, final boolean value)
  {
    box.setSelected(value);
  }

}
