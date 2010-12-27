package org.jetbrains.plugins.scheme.lexer;

import com.intellij.psi.tree.IElementType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.jetbrains.plugins.scheme.lexer.Tokens.*;

/**
 * @author Colin Fleming
 */
public class LexerTest
{
  @DataProvider(name = "goodCases")
  private static Object[][] lexerTestCases()
  {
    return testArray(testCase("(", LEFT_PAREN),
                     testCase("[", LEFT_SQUARE),
                     testCase("{", LEFT_CURLY),
                     testCase(")", RIGHT_PAREN),
                     testCase("]", RIGHT_SQUARE),
                     testCase("}", RIGHT_CURLY),
                     testCase("#(", OPEN_VECTOR),

                     testCase("'", QUOTE_MARK),
                     testCase("`", BACKQUOTE),
                     testCase(",", COMMA),
                     testCase(",@", COMMA_AT),

                     testCase(";", COMMENT),
                     testCase("; comment", COMMENT),
                     testCase("; comment\n", COMMENT),
                     testCase("; comment\r\n", COMMENT),
                     testCase("; comment\r", COMMENT),

                     testCase("\"string\"", STRING_LITERAL),
                     testCase("\"\"", STRING_LITERAL),
                     testCase("\"string", STRING_LITERAL),
                     testCase("\"str\\\"ng\"", STRING_LITERAL),
                     testCase("\"str\\$ng\"", STRING_LITERAL),

                     testCase("#\\newline", CHAR_LITERAL),
                     testCase("#\\space", CHAR_LITERAL),
                     testCase("#\\a", CHAR_LITERAL),

                     testCase("a", IDENTIFIER),
                     testCase("-", IDENTIFIER),
                     testCase("+", IDENTIFIER),
                     testCase("...", IDENTIFIER),
                     testCase("a-b", IDENTIFIER),
                     testCase("a.b", IDENTIFIER),

                     // Numbers
                     testCase("1", NUMBER_LITERAL),
                     testCase(".5", NUMBER_LITERAL),
                     testCase("1.5", NUMBER_LITERAL),
                     testCase("-17", NUMBER_LITERAL),
                     testCase("1/2", NUMBER_LITERAL),
                     testCase("-3/4", NUMBER_LITERAL),
                     testCase("8+0.6i", NUMBER_LITERAL),
                     testCase("1+2i", NUMBER_LITERAL),
                     testCase("3/4+1/2i", NUMBER_LITERAL),
                     testCase("2.0+0.3i", NUMBER_LITERAL),
                     testCase("#e0.5", NUMBER_LITERAL),
                     testCase("#x03bb", NUMBER_LITERAL),
                     testCase("#e1e10", NUMBER_LITERAL),
                     testCase("8.0@6.0", NUMBER_LITERAL),
                     testCase("8@6", NUMBER_LITERAL),
                     testCase("-0i", NUMBER_LITERAL),
                     testCase("-0.i", NUMBER_LITERAL),
                     testCase("+1i", NUMBER_LITERAL),
                     testCase("8+6e20i", NUMBER_LITERAL),
                     testCase("8e10+6i", NUMBER_LITERAL),
                     testCase("#d-0e-10-0e-0i", NUMBER_LITERAL),
                     testCase("#d#e-0.0f-0-.0s-0i", NUMBER_LITERAL),
                     testCase("1f2", NUMBER_LITERAL),
                     testCase("0@-.0", NUMBER_LITERAL),
                     testCase("999999999999999999999", NUMBER_LITERAL),

                     testCase("(define x 3)",
                              LEFT_PAREN,
                              IDENTIFIER,
                              WHITESPACE,
                              IDENTIFIER,
                              WHITESPACE,
                              NUMBER_LITERAL,
                              RIGHT_PAREN),

                     testCase("(gen-java0 #\\; #\\newline)",
                              LEFT_PAREN,
                              IDENTIFIER,
                              WHITESPACE,
                              CHAR_LITERAL,
                              WHITESPACE,
                              CHAR_LITERAL,
                              RIGHT_PAREN));
  }

  @Test(dataProvider = "goodCases")
  public static void testLexer(LexerTestCase testCase)
  {
    SchemeLexer lexer = new SchemeLexer();
    lexer.start(testCase.testData);

    for (int i = 0; i < testCase.types.length; i++)
    {
      IElementType expected = testCase.types[i];

      assert expected.equals(lexer.getTokenType()) : "Expected " + expected + ", got " + lexer.getTokenType();
      lexer.advance();
    }

    assert EOF.equals(lexer.getTokenType()) : "Expected final EOF, got " + lexer.getTokenType();
    lexer.advance();
    assert lexer.getTokenType() == null : "Expected final null, got " + lexer.getTokenType();
  }

  private static <T> T[] array(T... items)
  {
    return items;
  }

  static <T> Object[][] testArray(T... items)
  {
    Object[][] ret = new Object[items.length][];
    for (int i = 0; i < items.length; i++)
    {
      ret[i] = new Object[] { items[i] };
    }
    return ret;
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

    @Override
    public String toString()
    {
      return "LexerTestCase{" + testData + " -> " + (types == null ? null : Arrays.asList(types)) + '}';
    }
  }
}
