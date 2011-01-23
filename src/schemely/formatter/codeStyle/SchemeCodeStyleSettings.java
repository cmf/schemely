package schemely.formatter.codeStyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

import javax.swing.*;


public class SchemeCodeStyleSettings extends CustomCodeStyleSettings
{
  public String defineForms = "lambda\ndefine\nlet\nletrec\nlet*\ndefine-syntax\nlet-syntax\nletrec-syntax";

  protected SchemeCodeStyleSettings(CodeStyleSettings container)
  {
    super("SchemeCodeStyleSettings", container);
  }
}
