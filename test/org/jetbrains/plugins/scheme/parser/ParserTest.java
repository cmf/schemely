package org.jetbrains.plugins.scheme.parser;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.util.IncorrectOperationException;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.jetbrains.plugins.scheme.psi.api.SchemeList;
import org.junit.Test;


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
public class ParserTest extends TestCase
{
  protected Project myProject;
  protected Module myModule;
  protected IdeaProjectTestFixture myFixture;

  protected void setUp()
  {
    myFixture = createFixture();

    try
    {
      myFixture.setUp();
    }
    catch (Exception e)
    {
      throw new Error(e);
    }
    myModule = myFixture.getModule();
    myProject = myModule.getProject();
  }

  protected IdeaProjectTestFixture createFixture()
  {
    TestFixtureBuilder<IdeaProjectTestFixture>
      fixtureBuilder =
      IdeaTestFixtureFactory.getFixtureFactory().createLightFixtureBuilder();
    return fixtureBuilder.getFixture();
  }

  protected void tearDown()
  {
    try
    {
      myFixture.tearDown();
    }
    catch (Exception e)
    {
      throw new Error(e);
    }
  }

  private PsiFile createPseudoPhysicalFile(Project project, String fileName, String text) throws
                                                                                          IncorrectOperationException
  {
    FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
    PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
    return psiFileFactory.createFileFromText(fileName, fileType, text);
  }

  @Test
  public void testSchemeFileType()
  {
    Assert.assertNotNull(FileTypeManager.getInstance().getFileTypeByFileName("foo.scm"));
  }

  public PsiFile parseit(String contents)
  {
    PsiFile psiFile = createPseudoPhysicalFile(myProject, "test.scm", contents);
    String psiTree = DebugUtil.psiToString(psiFile, false);
    System.out.println(contents);
    System.out.println(psiTree);
    return psiFile;
  }

  @Test
  public void testSymbol()
  {
    parseit("foo");
  }

  @Test
  public void testSymbol2()
  {
    parseit("foo*");
  }

  @Test
  public void testInteger()
  {
    parseit("123");
  }

  @Test
  public void testFloat()
  {
    parseit("123.123");
  }

  @Test
  public void testString()
  {
    parseit("\"123.456\"");
  }

  public void testMultilineString()
  {
    parseit("\"this is\n" +
            "            a multiline\n" +
            "            string\"");
  }


  @Test
  public void testSexp1()
  {
    parseit("(a b)");
  }

  @Test
  public void testSexp2()
  {
    parseit("(a b (c d))");
  }

  @Test
  public void testQuote()
  {
    parseit("'(a b (c d))");
  }

  @Test
  public void testVector()
  {
    parseit("#(a b (c d))");
  }

  @Test
  public void testEmptyList()
  {
    parseit("()");
  }

  @Test
  public void testEmptyVector()
  {
    parseit("#()");
  }

  @Test
  public void testDottedList()
  {
    PsiFile psiFile = parseit("(a b . c)");
    PsiElement[] children = psiFile.getChildren();
    assert children.length == 1 : "Expecting 1 child, found " + children.length;
    PsiElement child = children[0];
    assert child instanceof SchemeList : "Expected list, found " + child.getClass().getName();
    assert ((SchemeList) child).isImproper() : "Expected dotted list!";
  }
}
