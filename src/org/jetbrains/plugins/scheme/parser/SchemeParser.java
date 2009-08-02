package org.jetbrains.plugins.scheme.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.SchemeBundle;
import org.jetbrains.plugins.scheme.lexer.Tokens;


/**
 * User: peter
 * Date: Nov 21, 2008
 * Time: 9:45:41 AM
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
public class SchemeParser implements PsiParser, Tokens
{
  @NotNull
  public ASTNode parse(IElementType root, PsiBuilder builder)
  {
    builder.setDebugMode(true);
    PsiBuilder.Marker marker = builder.mark();
    for (IElementType token = builder.getTokenType(); token != null; token = builder.getTokenType())
    {
      parseDatum(builder);
    }
    marker.done(AST.FILE);
    return builder.getTreeBuilt();
  }

  private void parseDatum(PsiBuilder builder)
  {
    IElementType token = builder.getTokenType();
    if (LEFT_PAREN == token)
    {
      parseList(builder);
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
    else if (SYNTACTIC_KEYWORDS.contains(token))
    {
      parseSymbol(builder);
    }
    else if (PREFIXES.contains(token))
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

  private void markAndAdvance(PsiBuilder builder, IElementType type)
  {
    markAndAdvance(builder).done(type);
  }

  private void internalError(String msg)
  {
    throw new Error(msg);
  }

  /**
   * Enter: Lexer is pointed at symbol
   * Exit: Lexer is pointed immediately after symbol
   *
   * @param builder
   */
  private void parseSymbol(PsiBuilder builder)
  {
    final PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer(); // eat atom
    marker.done(AST.SYMBOL);
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
   * Enter: Lexer is pointed at abbreviation mark
   * Exit: Lexer is pointed immediately after datum quoted by abbreviation mark
   */
  private void parseAbbreviation(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    parseDatum(builder);
    marker.done(AST.ABBREVIATION);
  }

  /**
   * Enter: Lexer is pointed at the opening left paren
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseList(PsiBuilder builder)
  {
    if (builder.getTokenType() != LEFT_PAREN)
    {
      internalError(SchemeBundle.message("expected.lparen"));
    }
    PsiBuilder.Marker marker = markAndAdvance(builder);

    IElementType token = builder.getTokenType();
    while (token != RIGHT_PAREN && token != null)
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

    if (builder.getTokenType() != RIGHT_PAREN)
    {
      builder.error(SchemeBundle.message("expected.token", RIGHT_PAREN.toString()));
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
}
