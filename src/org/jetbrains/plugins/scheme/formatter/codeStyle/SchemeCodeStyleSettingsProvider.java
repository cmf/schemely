package org.jetbrains.plugins.scheme.formatter.codeStyle;

import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.NotNull;


public class SchemeCodeStyleSettingsProvider extends CodeStyleSettingsProvider
{
  @NotNull
  public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings)
  {
    return new SchemeFormatConfigurable(settings, originalSettings);
  }

  @Override
  public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings)
  {
    return new SchemeCodeStyleSettings(settings);
  }
}
