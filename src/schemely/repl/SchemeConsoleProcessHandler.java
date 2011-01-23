package schemely.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;

import java.util.regex.Matcher;

public class SchemeConsoleProcessHandler extends ColoredProcessHandler
{
  private final LanguageConsoleImpl myLanguageConsole;

  public SchemeConsoleProcessHandler(Process process, String commandLine, LanguageConsoleImpl console)
  {
    super(process, commandLine, CharsetToolkit.UTF8_CHARSET);
    this.myLanguageConsole = console;
  }

  protected void textAvailable(String text, Key attributes)
  {
    // This is where we process input from the process
    String string = processPrompts(StringUtil.convertLineSeparators(text));
    SchemeConsoleHighlightingUtil.processOutput(this.myLanguageConsole, string, attributes);
  }

  private static String processPrompts(String text)
  {
    if (text != null)
    {
      String trimmed = text;
      Matcher matcher = SchemeConsoleHighlightingUtil.KAWA_PROMPT_PATTERN.matcher(trimmed);
      while (matcher.lookingAt())
      {
        String prefix = matcher.group();
        trimmed = StringUtil.trimStart(trimmed, prefix);
        matcher.reset(trimmed);
      }
      return trimmed;
    }
    return text;
  }

  public LanguageConsoleImpl getLanguageConsole()
  {
    return this.myLanguageConsole;
  }
}
