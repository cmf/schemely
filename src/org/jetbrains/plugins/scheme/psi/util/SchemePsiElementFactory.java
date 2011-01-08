package org.jetbrains.plugins.scheme.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;


public abstract class SchemePsiElementFactory
{
  public static SchemePsiElementFactory getInstance(Project project)
  {
    return ServiceManager.getService(project, SchemePsiElementFactory.class);
  }

  public abstract ASTNode createSymbolNodeFromText(@NotNull String newName);

  public abstract boolean hasSyntacticalErrors(@NotNull String text);

  public abstract SchemeFile createSchemeFileFromText(String text);
}
