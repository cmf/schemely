package schemely.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import schemely.SchemeBundle;
import schemely.lexer.Tokens;
import schemely.scheme.Scheme;
import schemely.scheme.SchemeImplementation;


public class SchemeParser implements PsiParser, Tokens
{
  private Scheme scheme;

  @NotNull
  public ASTNode parse(IElementType root, PsiBuilder builder)
  {
    scheme = SchemeImplementation.from(builder.getProject());

    builder.setDebugMode(true);
    PsiBuilder.Marker marker = builder.mark();
    for (IElementType token = builder.getTokenType(); token != null; token = builder.getTokenType())
    {
      parseDatum(builder);
    }
    marker.done(AST.FILE);
    return builder.getTreeBuilt();
  }

  protected void parseDatum(PsiBuilder builder)
  {
    IElementType token = builder.getTokenType();
    if (LEFT_PAREN == token)
    {
      parseList(builder, LEFT_PAREN, RIGHT_PAREN);
    }
    else if (LEFT_SQUARE == token && scheme.supportsSquareBracesForLists())
    {
      parseList(builder, LEFT_SQUARE, RIGHT_SQUARE);
    }
    else if (OPEN_VECTOR == token)
    {
      parseVector(builder);
    }
    else if (LITERALS.contains(token))
    {
      parseLiteral(builder);
    }
    else if (IDENTIFIER == token)
    {
      parseIdentifier(builder);
    }
    else if (SPECIAL == token)
    {
      parseSpecial(builder);
    }
    else if (getPrefixes().contains(token))
    {
      parseAbbreviation(builder);
    }
    else
    {
      syntaxError(builder, SchemeBundle.message("expected.left.paren.symbol.or.literal"));
    }
  }

  private void parseExpressions(IElementType endToken, PsiBuilder builder)
  {
    for (IElementType token = builder.getTokenType();
         token != endToken && token != null;
         token = builder.getTokenType())
    {
      parseDatum(builder);
    }
    if (builder.getTokenType() != endToken)
    {
      builder.error(SchemeBundle.message("expected.token", endToken.toString()));
    }
    else
    {
      builder.advanceLexer();
    }
  }

  private void syntaxError(PsiBuilder builder, String msg)
  {
    String e = msg + ": " + builder.getTokenText();
    builder.error(e);
    advanceLexerOrEOF(builder);
  }

  private void advanceLexerOrEOF(PsiBuilder builder)
  {
    if (builder.getTokenType() != null)
    {
      builder.advanceLexer();
    }
  }

  private PsiBuilder.Marker markAndAdvance(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    return marker;
  }

  private void internalError(String msg)
  {
    throw new Error(msg);
  }

  /**
   * Enter: Lexer is pointed at literal
   * Exit: Lexer is pointed immediately after literal
   *
   * @param builder
   */
  private void parseLiteral(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    marker.done(AST.LITERAL);
  }

  /**
   * Enter: Lexer is pointed at identifier
   * Exit: Lexer is pointed immediately after identifier
   *
   * @param builder
   */
  private void parseIdentifier(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    //    ParserUtils.getToken(builder, IDENTIFIER, "Expected identifier");
    // Currently using this for keywords too
    // TODO fix this
    builder.advanceLexer();
    marker.done(AST.IDENTIFIER);
  }

  /**
   * Enter: Lexer is pointed at special
   * Exit: Lexer is pointed immediately after special symbol
   *
   * @param builder
   */
  private void parseSpecial(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    marker.done(AST.SPECIAL);
  }

  /**
   * Enter: Lexer is pointed at abbreviation mark
   * Exit: Lexer is pointed immediately after datum quoted by abbreviation mark
   */
  private void parseAbbreviation(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    parseDatum(builder);
    marker.done(AST.QUOTED);
  }

  /**
   * Enter: Lexer is pointed at the opening left paren
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseList(PsiBuilder builder, IElementType open, IElementType close)
  {
    if (builder.getTokenType() != open)
    {
      internalError(SchemeBundle.message("expected.lparen"));
    }
    PsiBuilder.Marker marker = markAndAdvance(builder);

    IElementType token = builder.getTokenType();
    while (token != close && token != null)
    {
      if (token == DOT)
      {
        builder.advanceLexer();
        parseDatum(builder);
        break;
      }
      else
      {
        parseDatum(builder);
        token = builder.getTokenType();
      }
    }

    if (builder.getTokenType() != close)
    {
      builder.error(SchemeBundle.message("expected.token", close.toString()));
    }
    else
    {
      builder.advanceLexer();
    }
    marker.done(AST.LIST);
  }

  /**
   * Enter: Lexer is pointed at the opening left square
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseVector(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = markAndAdvance(builder);
    parseExpressions(RIGHT_PAREN, builder);
    marker.done(AST.VECTOR);
  }

  protected TokenSet getPrefixes()
  {
    return PREFIXES;
  }
}
