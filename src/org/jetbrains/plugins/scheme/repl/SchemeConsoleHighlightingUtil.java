package org.jetbrains.plugins.scheme.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.util.Key;

import java.util.regex.Pattern;

import org.intellij.lang.annotations.Language;

public class SchemeConsoleHighlightingUtil
{
  @Language("RegExp")
  public static final String KAWA_PROMPT = "#\\|[^\\|]*\\|#\\s*";

  public static final Pattern KAWA_PROMPT_PATTERN = Pattern.compile(KAWA_PROMPT);

  public static void processOutput(LanguageConsoleImpl console, String text, Key attributes)
  {
    ConsoleViewContentType outputType = ConsoleViewContentType.NORMAL_OUTPUT;

    LanguageConsoleImpl.printToConsole(console, text, outputType, null);
  }
}
