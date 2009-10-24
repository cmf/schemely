package org.jetbrains.plugins.scheme.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.plugins.scheme.psi.stubs.elements.SchemeStubFileElementType;

public interface AST extends Tokens
{
  final IStubFileElementType FILE = new SchemeStubFileElementType();

  final IElementType LIST = new SchemeElementType("list");
  final IElementType VECTOR = new SchemeElementType("vector");

  final IElementType LITERAL = new SchemeElementType("literal");
  final IElementType IDENTIFIER = new SchemeElementType("identifier");

  final IElementType ABBREVIATION = new SchemeElementType("abbreviation");

  TokenSet LIST_LIKE_FORMS = TokenSet.create(LIST, VECTOR);

  TokenSet BRACES = TokenSet.create(LEFT_CURLY, LEFT_PAREN, LEFT_SQUARE, RIGHT_CURLY, RIGHT_PAREN, RIGHT_SQUARE);

  TokenSet MODIFIERS = TokenSet.create(QUOTE_MARK, BACKQUOTE, COMMA, COMMA_AT);
}
