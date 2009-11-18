package org.jetbrains.plugins.scheme.utils;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.plugins.scheme.lexer.SchemeLexer;
import org.jetbrains.plugins.scheme.lexer.Tokens;


public class SchemeNamesUtil
{
  public static boolean isIdentifier(String text)
  {
    ApplicationManager.getApplication().assertReadAccessAllowed();
    if (text == null)
    {
      return false;
    }
    Lexer lexer = new SchemeLexer();
    lexer.start(text, 0, text.length(), 0);
    if (lexer.getTokenType() != Tokens.IDENTIFIER)
    {
      return false;
    }
    lexer.advance();
    return lexer.getTokenType() == null;
  }
}
