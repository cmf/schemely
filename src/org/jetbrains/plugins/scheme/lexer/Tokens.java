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

  IElementType TRUE = new SchemeElementType("true");
  IElementType FALSE = new SchemeElementType("false");

  TokenSet LITERALS = TokenSet.create(STRING_LITERAL, NUMBER_LITERAL, CHAR_LITERAL, TRUE, FALSE);

  IElementType IDENTIFIER = new SchemeElementType("identifier");

  IElementType DOT = new SchemeElementType(".");
  IElementType DOTDOTDOT = new SchemeElementType("...");

  IElementType ARROW = new SchemeElementType("=>");

  IElementType QUOTE = new SchemeElementType("quote");
  IElementType QUASIQUOTE = new SchemeElementType("quasiquote");
  IElementType UNQUOTE = new SchemeElementType("unquote");
  IElementType UNQUOTE_SPLICING = new SchemeElementType("unquote-splicing");
  IElementType LAMBDA = new SchemeElementType("lambda");
  IElementType DEFINE = new SchemeElementType("define");
  IElementType DEFINE_SYNTAX = new SchemeElementType("define-syntax");
  IElementType IF = new SchemeElementType("if");
  IElementType ELSE = new SchemeElementType("else");
  IElementType LET = new SchemeElementType("let");
  IElementType LET_STAR = new SchemeElementType("let*");
  IElementType LETREC = new SchemeElementType("letrec");
  IElementType SET = new SchemeElementType("set!");
  IElementType BEGIN = new SchemeElementType("begin");
  IElementType COND = new SchemeElementType("cond");
  IElementType AND = new SchemeElementType("and");
  IElementType OR = new SchemeElementType("or");
  IElementType CASE = new SchemeElementType("case");
  IElementType DO = new SchemeElementType("do");
  IElementType DELAY = new SchemeElementType("delay");
  IElementType LET_SYNTAX = new SchemeElementType("let-syntax");
  IElementType LETREC_SYNTAX = new SchemeElementType("letrec-syntax");

  // Control characters
  IElementType EOL = new SchemeElementType("end of line");
  IElementType EOF = new SchemeElementType("end of file");
  IElementType WHITESPACE = TokenType.WHITE_SPACE;
  IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;


  // Useful token sets
  TokenSet WHITESPACE_SET = TokenSet.create(EOL, EOF, WHITESPACE);
  TokenSet IDENTIFIERS = TokenSet.create(IDENTIFIER);
  TokenSet STRINGS = TokenSet.create(STRING_LITERAL);

  TokenSet
    EXPRESSION_KEYWORDS =
    TokenSet.create(QUOTE, LAMBDA, IF, SET, BEGIN, COND, AND, OR, CASE, LET, LET_STAR, LETREC, DO, DELAY, QUASIQUOTE);
  TokenSet SYNTACTIC_EXTRA_KEYWORDS = TokenSet.create(ELSE, ARROW, DEFINE, UNQUOTE, UNQUOTE_SPLICING);
  TokenSet SYNTACTIC_KEYWORDS = TokenSet.orSet(EXPRESSION_KEYWORDS, SYNTACTIC_EXTRA_KEYWORDS);

  TokenSet PREFIXES = TokenSet.create(QUOTE_MARK, BACKQUOTE, COMMA, COMMA_AT);
}
