package org.jetbrains.plugins.scheme.parser;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.scheme.psi.api.SchemeAbbreviation;
import org.jetbrains.plugins.scheme.psi.api.SchemeList;
import org.jetbrains.plugins.scheme.psi.api.SchemeLiteral;
import org.jetbrains.plugins.scheme.psi.api.SchemeVector;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;
import org.testng.annotations.Test;


/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 5, 2009
 * Time: 2:11:20 PM
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
  public void testQuote()
  {
    element("'(a b (c d))", SchemeAbbreviation.class);
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

  private SchemeList list(String contents)
  {
    return element(contents, SchemeList.class);
  }

  private void literal(String contents)
  {
    element(contents, SchemeLiteral.class);
  }

  private void identifier(String contents)
  {
    element(contents, SchemeIdentifier.class);
  }

  private <T extends PsiElement> T element(String contents, Class<T> theClass)
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
