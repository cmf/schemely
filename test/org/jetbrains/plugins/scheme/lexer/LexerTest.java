package org.jetbrains.plugins.scheme.lexer;

import com.intellij.psi.tree.IElementType;
import junit.framework.TestCase;
import static org.jetbrains.plugins.scheme.lexer.Tokens.*;

/**
 * @author Colin Fleming
 */
public class LexerTest extends TestCase
{
  private static final LexerTestCase[] cases =
    array(
      // Numbers
      testCase(" 1 ", NUMBER_LITERAL),
      testCase(" .5 ", NUMBER_LITERAL),
      testCase(" 1.5 ", NUMBER_LITERAL),
      testCase(" -17 ", NUMBER_LITERAL),
      testCase(" 1/2 ", NUMBER_LITERAL),
      testCase(" -3/4 ", NUMBER_LITERAL),
      testCase(" 8+0.6i ", NUMBER_LITERAL),
      testCase(" 1+2i ", NUMBER_LITERAL),
      testCase(" 3/4+1/2i ", NUMBER_LITERAL),
      testCase(" 2.0+0.3i ", NUMBER_LITERAL),
      testCase(" #e0.5 ", NUMBER_LITERAL),
      testCase(" #x03bb ", NUMBER_LITERAL),
      testCase(" #e1e10 ", NUMBER_LITERAL),
      testCase(" 8.0@6.0 ", NUMBER_LITERAL),
      testCase(" 8@6 ", NUMBER_LITERAL),
      testCase(" -0i ", NUMBER_LITERAL),
      testCase(" -0.i ", NUMBER_LITERAL),
      testCase(" +1i ", NUMBER_LITERAL),
      testCase(" 8+6e20i ", NUMBER_LITERAL),
      testCase(" 8e10+6i ", NUMBER_LITERAL),
      testCase(" #d-0e-10-0e-0i ", NUMBER_LITERAL),
      testCase(" #d#e-0.0f-0-.0s-0i ", NUMBER_LITERAL),
      testCase(" 1f2 ", NUMBER_LITERAL),
      testCase(" 0@-.0 ", NUMBER_LITERAL),
      testCase(" 999999999999999999999 ", NUMBER_LITERAL),

      testCase(" (define x 3) ", LEFT_PAREN, IDENTIFIER, WHITESPACE, IDENTIFIER, WHITESPACE, NUMBER_LITERAL, RIGHT_PAREN)
    );

  private static final LexerTestCase[] badCases =
    array(
      // Numbers
      testCase(" 1a ", NUMBER_LITERAL)
    );

  public static void testLexer()
  {
    for (LexerTestCase testCase : cases)
    {
      SchemeFlexLexer lexer = new SchemeFlexLexer();
      lexer.start(testCase.testData);

      assertEquals("Expected initial whitespace", WHITESPACE, lexer.getTokenType());
      lexer.advance();

      for (int i = 0; i < testCase.types.length; i++)
      {
        IElementType expected = testCase.types[i];

        assertEquals("Incorrect token type", expected, lexer.getTokenType());
        lexer.advance();
      }

      assertEquals("Expected final whitespace", WHITESPACE, lexer.getTokenType());
    }
  }

  public static void testBadCases()
  {
    for (LexerTestCase badCase : badCases)
    {
      SchemeFlexLexer lexer = new SchemeFlexLexer();
      lexer.start(badCase.testData);
      for (int i = 0; i < badCase.types.length; i++)
      {
        IElementType expected = badCase.types[i];
        assertEquals("Incorrect token type", expected, lexer.getTokenType());
        lexer.advance();
      }

      assertEquals("Expected bad token", BAD_CHARACTER, lexer.getTokenType());
    }
  }

  private static <T> T[] array(T... items)
  {
    return items;
  }

  private static LexerTestCase testCase(String testData, IElementType... types)
  {
    return new LexerTestCase(testData, types);
  }

  private static class LexerTestCase
  {
    private final String testData;
    private final IElementType[] types;

    private LexerTestCase(String testData, IElementType... types)
    {
      this.testData = testData;
      this.types = types;
    }
  }
}
