package org.jetbrains.plugins.scheme.formatter.codeStyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;


public class SchemeCodeStyleSettings extends CustomCodeStyleSettings
{
  public boolean ALIGN_SCHEME_FORMS = false;

  protected SchemeCodeStyleSettings(CodeStyleSettings container)
  {
    super("SchemeCodeStyleSettings", container);
  }
}
