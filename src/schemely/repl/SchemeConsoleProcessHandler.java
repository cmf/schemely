package schemely.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import schemely.scheme.Scheme;
import schemely.scheme.SchemeImplementation;

public class SchemeConsoleProcessHandler extends ColoredProcessHandler
{
  private final LanguageConsoleImpl myLanguageConsole;

  public SchemeConsoleProcessHandler(Process process, String commandLine, LanguageConsoleImpl console)
  {
    super(process, commandLine, CharsetToolkit.UTF8_CHARSET);
    myLanguageConsole = console;
  }

  @Override
  protected void textAvailable(String text, Key attributes)
  {
    // This is where we process input from the process
    Scheme scheme = SchemeImplementation.from(myLanguageConsole.getProject());
    scheme.getRepl().processOutput(myLanguageConsole, StringUtil.convertLineSeparators(text), attributes);
  }

  public LanguageConsoleImpl getLanguageConsole()
  {
    return myLanguageConsole;
  }
}
