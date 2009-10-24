package org.jetbrains.plugins.scheme.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class SchemeFlexLexer extends FlexAdapter
{
  public SchemeFlexLexer()
  {
    super(new _SchemeLexer((Reader) null));
  }
}