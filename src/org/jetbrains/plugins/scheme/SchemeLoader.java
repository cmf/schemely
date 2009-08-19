package org.jetbrains.plugins.scheme;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 16, 2009
 * Time: 4:34:18 PM
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
public class SchemeLoader implements ApplicationComponent
{
  public SchemeLoader()
  {
  }

  public void initComponent()
  {
    loadScheme();
  }

  private void loadScheme()
  {
    ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerAdapter()
    {
      public void projectOpened(Project project)
      {
      }
    });

  }

  public void disposeComponent()
  {
  }

  @NotNull
  public String getComponentName()
  {
    return "scheme.support.loader";
  }

}
