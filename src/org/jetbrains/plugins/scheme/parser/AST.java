package org.jetbrains.plugins.scheme.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.plugins.scheme.psi.ClStubElementType;
import org.jetbrains.plugins.scheme.psi.api.defs.ClDef;
import org.jetbrains.plugins.scheme.psi.stubs.api.ClDefStub;
import org.jetbrains.plugins.scheme.psi.stubs.elements.ClDefElementType;
import org.jetbrains.plugins.scheme.psi.stubs.elements.ClDefMethodElementType;
import org.jetbrains.plugins.scheme.psi.stubs.elements.ClStubFileElementType;

/**
 * User: peter
 * Date: Nov 21, 2008
 * Time: 9:46:12 AM
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
public interface AST extends Tokens
{
  final IStubFileElementType FILE = new ClStubFileElementType();

  final IElementType LIST = new SchemeElementType("list");
  final IElementType VECTOR = new SchemeElementType("vector");

  final ClStubElementType<ClDefStub, ClDef> DEF = new ClDefElementType();
  final ClStubElementType<ClDefStub, ClDef> DEFMETHOD = new ClDefMethodElementType();

  final IElementType LITERAL = new SchemeElementType("literal");
  final IElementType IDENTIFIER = new SchemeElementType("identifier");
  final IElementType SYMBOL = new SchemeElementType("symbol");
  final IElementType KEYWORD = new SchemeElementType("key definition");

  final IElementType ABBREVIATION = new SchemeElementType("abbreviation");

  TokenSet LIST_LIKE_FORMS = TokenSet.create(LIST, VECTOR, DEF, DEFMETHOD);

  TokenSet BRACES = TokenSet.create(LEFT_CURLY, LEFT_PAREN, LEFT_SQUARE, RIGHT_CURLY, RIGHT_PAREN, RIGHT_SQUARE);

  TokenSet MODIFIERS = TokenSet.create(QUOTE, BACKQUOTE, COMMA, COMMA_AT);
}
