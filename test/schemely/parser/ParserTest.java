package schemely.parser;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.intellij.lang.annotations.Language;
import schemely.psi.impl.SchemeQuoted;
import schemely.psi.impl.SchemeLiteral;
import schemely.psi.impl.SchemeVector;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;
import org.testng.annotations.Test;


public class ParserTest extends ParserTestBase
{
  @Test
  public void testSchemeFileType()
  {
    assert FileTypeManager.getInstance().getFileTypeByFileName("foo.scm") != FileTypes.UNKNOWN :
      "Scheme file not recognised!";
  }

  @Test
  public void testSymbol()
  {
    identifier("foo");
  }

  @Test
  public void testSymbol2()
  {
    identifier("foo*");
  }

  @Test
  public void testInteger()
  {
    literal("123");
  }

  @Test
  public void testFloat()
  {
    literal("123.123");
  }

  @Test
  public void testString()
  {
    literal("\"123.456\"");
  }

  public void testMultilineString()
  {
    literal("\"this is\n" + "            a multiline\n" + "            string\"");
  }

  @Test
  public void testSexp1()
  {
    list("(a b)");
  }


  @Test
  public void testSexp2()
  {
    list("(a b (c d))");
  }

  @Test
  public void testQuotedList()
  {
    element("'(a b (c d))", SchemeQuoted.class);
  }

  @Test
  public void testQuotedIdentifier()
  {
    element("'a", SchemeQuoted.class);
  }

  @Test
  public void testVector()
  {
    element("#(a b (c d))", SchemeVector.class);
  }

  @Test
  public void testEmptyList()
  {
    list("()");
  }

  @Test
  public void testEmptyVector()
  {
    element("#()", SchemeVector.class);
  }

  @Test
  public void testDottedList()
  {
    assert list("(a b . c)").isImproper() : "Expected dotted list!";
  }

  private SchemeList list(@Language("Scheme") String contents)
  {
    return element(contents, SchemeList.class);
  }

  private void literal(@Language("Scheme") String contents)
  {
    element(contents, SchemeLiteral.class);
  }

  private void identifier(@Language("Scheme") String contents)
  {
    element(contents, SchemeIdentifier.class);
  }

  private <T extends PsiElement> T element(@Language("Scheme") String contents, Class<T> theClass)
  {
    PsiFile psiFile = parse(contents);
    PsiElement[] children = psiFile.getChildren();
    assert children.length == 1 : "Expecting 1 child, found " + children.length;
    PsiElement child = children[0];
    assert theClass.isAssignableFrom(child.getClass()) :
      "Expected " + theClass.getName() + ", found " + child.getClass().getName();
    return theClass.cast(child);
  }
}
