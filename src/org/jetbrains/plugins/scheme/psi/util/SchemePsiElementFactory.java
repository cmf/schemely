package org.jetbrains.plugins.scheme.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public abstract class SchemePsiElementFactory
{
  public static SchemePsiElementFactory getInstance(Project project)
  {
    return ServiceManager.getService(project, SchemePsiElementFactory.class);
  }

  public abstract ASTNode createSymbolNodeFromText(@NotNull String newName);

}
