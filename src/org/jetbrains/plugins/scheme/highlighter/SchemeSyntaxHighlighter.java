package org.jetbrains.plugins.scheme.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.lexer.SchemeFlexLexer;
import org.jetbrains.plugins.scheme.lexer.Tokens;

import java.util.HashMap;
import java.util.Map;

public class SchemeSyntaxHighlighter extends SyntaxHighlighterBase implements Tokens
{
  private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

  static final TokenSet sNUMBERS = TokenSet.create(NUMBER_LITERAL);
  static final TokenSet sLINE_COMMENTS = TokenSet.create(Tokens.COMMENT);
  static final TokenSet sLITERALS = TokenSet.create(Tokens.TRUE, Tokens.FALSE);
  static final TokenSet sSTRINGS = Tokens.STRINGS;
  static final TokenSet sCHARS = TokenSet.create(Tokens.CHAR_LITERAL);
  static final TokenSet sPARENS = TokenSet.create(Tokens.LEFT_PAREN, Tokens.RIGHT_PAREN);

  static final
  TokenSet
    sBRACES =
    TokenSet.create(Tokens.LEFT_SQUARE, Tokens.RIGHT_SQUARE, Tokens.LEFT_CURLY, Tokens.RIGHT_CURLY);

  @NotNull
  public Lexer getHighlightingLexer()
  {
    return new SchemeFlexLexer();
  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType)
  {
    return pack(ATTRIBUTES.get(tokenType));
  }

  @NonNls
  static final String LINE_COMMENT_ID = "Scheme Line comment";
  @NonNls
  static final String IDENTIFIER_ID = "Identifier";
  @NonNls
  static final String NUMBER_ID = "Scheme Numbers";
  @NonNls
  static final String STRING_ID = "Scheme Strings";
  @NonNls
  static final String BAD_CHARACTER_ID = "Bad character";
  @NonNls
  static final String BRACES_ID = "Scheme Braces";
  @NonNls
  static final String PAREN_ID = "Scheme Parentheses";
  @NonNls
  static final String LITERAL_ID = "Scheme Literal";
  @NonNls
  static final String CHAR_ID = "Scheme Character";
  @NonNls
  static final String QUOTED_ID = "Quoted text";
  @NonNls
  static final String KEYWORD_ID = "Keyword";

  public static final TextAttributes ATOM_ATTRIB = defaultFor(HighlighterColors.TEXT).clone();


  // Registering TextAttributes
  static
  {
    createTextAttributesKey(LINE_COMMENT_ID, defaultFor(SyntaxHighlighterColors.LINE_COMMENT));
    //    TextAttributesKey.createTextAttributesKey(KEY_ID,
    //                                              HighlightInfoType.STATIC_FIELD.getAttributesKey().getDefaultAttributes());
    createTextAttributesKey(IDENTIFIER_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
    createTextAttributesKey(NUMBER_ID, defaultFor(SyntaxHighlighterColors.NUMBER));
    createTextAttributesKey(STRING_ID, defaultFor(SyntaxHighlighterColors.STRING));
    createTextAttributesKey(BRACES_ID, defaultFor(SyntaxHighlighterColors.BRACES));
    createTextAttributesKey(PAREN_ID, defaultFor(SyntaxHighlighterColors.PARENTHS));
    createTextAttributesKey(LITERAL_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
    createTextAttributesKey(CHAR_ID, defaultFor(SyntaxHighlighterColors.STRING));
    createTextAttributesKey(BAD_CHARACTER_ID, defaultFor(HighlighterColors.BAD_CHARACTER));
    createTextAttributesKey(QUOTED_ID, defaultFor(HighlighterColors.TEXT));
    createTextAttributesKey(KEYWORD_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));

    //    Color deepBlue = SyntaxHighlighterColors.KEYWORD.getDefaultAttributes().getForegroundColor();
    //    ATOM_ATTRIB.setForegroundColor(deepBlue);
  }

  public static TextAttributesKey LINE_COMMENT = createTextAttributesKey(LINE_COMMENT_ID);
  public static TextAttributesKey IDENTIFIER = createTextAttributesKey(IDENTIFIER_ID);
  public static TextAttributesKey NUMBER = createTextAttributesKey(NUMBER_ID);
  public static TextAttributesKey STRING = createTextAttributesKey(STRING_ID);
  public static TextAttributesKey BRACES = createTextAttributesKey(BRACES_ID);
  public static TextAttributesKey PARENS = createTextAttributesKey(PAREN_ID);
  public static TextAttributesKey LITERAL = createTextAttributesKey(LITERAL_ID);
  public static TextAttributesKey CHAR = createTextAttributesKey(CHAR_ID);
  public static TextAttributesKey BAD_CHARACTER = createTextAttributesKey(BAD_CHARACTER_ID);
  public static TextAttributesKey QUOTED = createTextAttributesKey(QUOTED_ID);
  public static TextAttributesKey KEYWORD = createTextAttributesKey(KEYWORD_ID);

  static
  {
    fillMap(ATTRIBUTES, sLINE_COMMENTS, LINE_COMMENT);
    fillMap(ATTRIBUTES, sNUMBERS, NUMBER);
    fillMap(ATTRIBUTES, sSTRINGS, STRING);
    fillMap(ATTRIBUTES, sBRACES, BRACES);
    fillMap(ATTRIBUTES, sPARENS, PARENS);
    fillMap(ATTRIBUTES, sLITERALS, LITERAL);
    fillMap(ATTRIBUTES, sCHARS, CHAR);
  }

  private static TextAttributes defaultFor(TextAttributesKey key)
  {
    return key.getDefaultAttributes();
  }
}
