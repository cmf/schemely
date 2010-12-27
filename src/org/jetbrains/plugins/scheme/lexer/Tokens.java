package org.jetbrains.plugins.scheme.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.scheme.parser.SchemeElementType;

public interface Tokens
{
  // Special characters
  IElementType OPEN_VECTOR = new SchemeElementType("#(");
  IElementType LEFT_PAREN = new SchemeElementType("(");
  IElementType RIGHT_PAREN = new SchemeElementType(")");

  IElementType LEFT_CURLY = new SchemeElementType("{");
  IElementType RIGHT_CURLY = new SchemeElementType("}");

  IElementType LEFT_SQUARE = new SchemeElementType("[");
  IElementType RIGHT_SQUARE = new SchemeElementType("]");

  IElementType QUOTE_MARK = new SchemeElementType("'");
  IElementType BACKQUOTE = new SchemeElementType("`");
  IElementType COMMA = new SchemeElementType(",");
  IElementType COMMA_AT = new SchemeElementType(",@");

  // Comments
  IElementType COMMENT = new SchemeElementType("comment");

  TokenSet COMMENTS = TokenSet.create(COMMENT);

  // Literals
  IElementType STRING_LITERAL = new SchemeElementType("string literal");
  IElementType NUMBER_LITERAL = new SchemeElementType("number literal");
  IElementType CHAR_LITERAL = new SchemeElementType("character literal");
  IElementType BOOLEAN_LITERAL = new SchemeElementType("boolean literal");

  TokenSet LITERALS = TokenSet.create(STRING_LITERAL, NUMBER_LITERAL, CHAR_LITERAL, BOOLEAN_LITERAL);

  IElementType IDENTIFIER = new SchemeElementType("identifier");

  IElementType DOT = new SchemeElementType(".");

  IElementType ARROW = new SchemeElementType("=>");

  IElementType SPECIAL = new SchemeElementType("special");

  // Control characters
  IElementType EOF = new SchemeElementType("end of file");
  IElementType WHITESPACE = TokenType.WHITE_SPACE;
  IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

  // Useful token sets
  TokenSet WHITESPACE_SET = TokenSet.create(EOF, WHITESPACE);
  TokenSet IDENTIFIERS = TokenSet.create(IDENTIFIER);
  TokenSet STRINGS = TokenSet.create(STRING_LITERAL);

  TokenSet PREFIXES = TokenSet.create(QUOTE_MARK, BACKQUOTE, COMMA, COMMA_AT);
}
