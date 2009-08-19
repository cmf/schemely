package org.jetbrains.plugins.scheme.highlighter;

import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.lexer.SchemeFlexLexer;
import org.jetbrains.plugins.scheme.lexer.Tokens;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * User: peter
 * Date: Dec 8, 2008
 * Time: 9:00:27 AM
 * Copyright 2007, 2008, 2009 Red Shark Technology
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SchemeSyntaxHighlighter extends SyntaxHighlighterBase implements Tokens
{
  private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

  static final TokenSet sNUMBERS = TokenSet.create(NUMBER_LITERAL);

  static final TokenSet sLINE_COMMENTS = TokenSet.create(Tokens.COMMENT);

  static final TokenSet sBAD_CHARACTERS = TokenSet.create(Tokens.BAD_CHARACTER);

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
  static final String KEY_ID = "Scheme Keyword";
  @NonNls
  static final String DEF_ID = "First symbol in list";
  @NonNls
  static final String ATOM_ID = "Scheme Atom";
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

  public static final TextAttributes ATOM_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();


  // Registering TextAttributes
  static
  {
    TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID,
                                              SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(KEY_ID,
                                              HighlightInfoType.STATIC_FIELD.getAttributesKey().getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(DEF_ID, SyntaxHighlighterColors.KEYWORD.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(NUMBER_ID, SyntaxHighlighterColors.NUMBER.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(STRING_ID, SyntaxHighlighterColors.STRING.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(BRACES_ID, SyntaxHighlighterColors.BRACES.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(PAREN_ID, SyntaxHighlighterColors.PARENTHS.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(LITERAL_ID, SyntaxHighlighterColors.KEYWORD.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(CHAR_ID, SyntaxHighlighterColors.STRING.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID, HighlighterColors.BAD_CHARACTER.getDefaultAttributes());

    Color deepBlue = SyntaxHighlighterColors.KEYWORD.getDefaultAttributes().getForegroundColor();
    ATOM_ATTRIB.setForegroundColor(deepBlue);
    TextAttributesKey.createTextAttributesKey(ATOM_ID, ATOM_ATTRIB);
  }

  public static TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID);
  public static TextAttributesKey KEY = TextAttributesKey.createTextAttributesKey(KEY_ID);
  public static TextAttributesKey DEF = TextAttributesKey.createTextAttributesKey(DEF_ID);
  public static TextAttributesKey ATOM = TextAttributesKey.createTextAttributesKey(ATOM_ID);
  public static TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(NUMBER_ID);
  public static TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(STRING_ID);
  public static TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey(BRACES_ID);
  public static TextAttributesKey PARENTS = TextAttributesKey.createTextAttributesKey(PAREN_ID);
  public static TextAttributesKey LITERAL = TextAttributesKey.createTextAttributesKey(LITERAL_ID);
  public static TextAttributesKey CHAR = TextAttributesKey.createTextAttributesKey(CHAR_ID);
  public static TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID);


  static
  {
    fillMap(ATTRIBUTES, sLINE_COMMENTS, LINE_COMMENT);
    fillMap(ATTRIBUTES, sNUMBERS, NUMBER);
    fillMap(ATTRIBUTES, sSTRINGS, STRING);
    fillMap(ATTRIBUTES, sBRACES, BRACES);
    fillMap(ATTRIBUTES, sPARENS, PARENTS);
    fillMap(ATTRIBUTES, sLITERALS, LITERAL);
    fillMap(ATTRIBUTES, sCHARS, CHAR);
    fillMap(ATTRIBUTES, Tokens.SYNTACTIC_KEYWORDS, DEF);
  }
}
