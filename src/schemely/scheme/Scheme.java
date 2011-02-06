package schemely.scheme;

import com.intellij.execution.CantRunException;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Key;
import com.intellij.util.NotNullFunction;

import java.util.List;

/**
 * @author Colin Fleming
 */
public interface Scheme
{
  REPL getRepl();

  interface REPL
  {
    List<String> createRuntimeArgs(Module module, String workingDir) throws CantRunException;

    void processOutput(LanguageConsoleImpl console, String text, Key attributes);

    NotNullFunction<String, Boolean> getConsoleMatcher();
  }
}
