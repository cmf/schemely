package schemely.scheme.sisc;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.util.text.StringUtil;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NonNls;
import schemely.repl.SchemeConsole;
import schemely.utils.Editors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Colin Fleming
 */
public class SISCOutputProcessor
{
  enum State { STARTING, EXECUTING }

  private State state = State.STARTING;

  @NonNls
  @Language("RegExp")
  public static final String SISC_PROMPT = "#;>\\s*";
  public static final Pattern SISC_PROMPT_PATTERN = Pattern.compile(SISC_PROMPT);

  private final SchemeConsole console;

  private final StringBuilder buffer = new StringBuilder();

  public SISCOutputProcessor(SchemeConsole console)
  {
    this.console = console;
  }

  public void processOutput(String text)
  {
    if (text != null)
    {
      boolean sawPrompt = false;

      String trimmed = text;
      Matcher matcher = SISC_PROMPT_PATTERN.matcher(trimmed);
      while (matcher.lookingAt())
      {
        sawPrompt = true;
        String prefix = matcher.group();
        trimmed = StringUtil.trimStart(trimmed, prefix);
        matcher.reset(trimmed);
      }

      if (sawPrompt)
      {
        if (state == State.STARTING)
        {
          state = State.EXECUTING;
        }
        else
        {
          flush();
        }
        buffer.append(trimmed);
      }
      else
      {
        if (state == State.STARTING)
        {
          LanguageConsoleImpl.printToConsole(console, trimmed, ConsoleViewContentType.NORMAL_OUTPUT, null);
          Editors.scrollDown(console.getHistoryViewer());
        }
        else
        {
          buffer.append(trimmed);
        }
      }
    }
  }

  public void flush()
  {
    LanguageConsoleImpl.printToConsole(console, buffer.toString(), ConsoleViewContentType.NORMAL_OUTPUT, null);
    Editors.scrollDown(console.getHistoryViewer());
    buffer.setLength(0);
  }
}
