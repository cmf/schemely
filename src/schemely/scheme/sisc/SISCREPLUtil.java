package schemely.scheme.sisc;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.util.text.StringUtil;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NonNls;
import schemely.utils.Editors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Colin Fleming
 */
public class SISCREPLUtil
{
  @NonNls
  @Language("RegExp")
  public static final String SISC_PROMPT = "#;>\\s*";
  public static final Pattern SISC_PROMPT_PATTERN = Pattern.compile(SISC_PROMPT);

  //  @Override
  public static void processOutput(LanguageConsoleImpl console, String text)
  {
    ConsoleViewContentType outputType = ConsoleViewContentType.NORMAL_OUTPUT;
    LanguageConsoleImpl.printToConsole(console, processPrompts(text), outputType, null);
    Editors.scrollDown(console.getHistoryViewer());
  }

  private static String processPrompts(String text)
  {
    if (text != null)
    {
      String trimmed = text;
      Matcher matcher = SISC_PROMPT_PATTERN.matcher(trimmed);
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
}
