package org.jetbrains.plugins.scheme.highlighter;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.lexer.Tokens;

/**
 * User: peter
 * Date: Nov 20, 2008
 * Time: 11:11:11 AM
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
public class SchemeBraceMatcher implements PairedBraceMatcher
{
  private static final
  BracePair[]
    PAIRS =
    new BracePair[]{new BracePair(Tokens.LEFT_PAREN, Tokens.RIGHT_PAREN, true),
                    new BracePair(Tokens.LEFT_SQUARE, Tokens.RIGHT_SQUARE, true),
                    new BracePair(Tokens.LEFT_CURLY, Tokens.RIGHT_CURLY, true),};

  public BracePair[] getPairs()
  {
    return PAIRS;
  }

  public boolean isPairedBracesAllowedBeforeType(@NotNull final IElementType lbraceType,
                                                 @Nullable final IElementType tokenType)
  {
    return tokenType == null ||
           Tokens.WHITESPACE_SET.contains(tokenType) ||
           Tokens.COMMENTS.contains(tokenType) ||
           tokenType == Tokens.COMMA ||
           tokenType == Tokens.RIGHT_SQUARE ||
           tokenType == Tokens.RIGHT_PAREN ||
           tokenType == Tokens.RIGHT_CURLY;
  }

  public int getCodeConstructStart(PsiFile file, int openingBraceOffset)
  {
    return openingBraceOffset;
  }
}
