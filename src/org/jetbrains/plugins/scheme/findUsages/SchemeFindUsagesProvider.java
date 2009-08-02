package org.jetbrains.plugins.scheme.findUsages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.lexer.SchemeFlexLexer;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.plugins.scheme.psi.api.defs.ClDef;
import org.jetbrains.plugins.scheme.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public class SchemeFindUsagesProvider implements FindUsagesProvider
{
  @Nullable
  public WordsScanner getWordsScanner()
  {
    return new DefaultWordsScanner(new SchemeFlexLexer(), Tokens.IDENTIFIERS, Tokens.COMMENTS, Tokens.STRINGS);
  }

  public boolean canFindUsagesFor(@NotNull PsiElement psiElement)
  {
    return psiElement instanceof ClDef || psiElement instanceof ClSymbol;
  }

  public String getHelpId(@NotNull PsiElement psiElement)
  {
    return null;
  }

  @NotNull
  public String getType(@NotNull PsiElement element)
  {
    if (element instanceof ClSymbol)
    {
      return "symbol";
    }
    if (element instanceof ClDef)
    {
      return "definition";
    }
    return "enitity";
  }

  @NotNull
  public String getDescriptiveName(@NotNull PsiElement element)
  {
    if (element instanceof ClSymbol)
    {
      ClSymbol symbol = (ClSymbol) element;
      final String name = symbol.getText();
      return name == null ? symbol.getText() : name;
    }
    if (element instanceof ClDef)
    {
      ClDef def = (ClDef) element;
      return def.getPresentationText();
    }
    return element.getText();
  }

  @NotNull
  public String getNodeText(@NotNull PsiElement element, boolean useFullName)
  {
    if (element instanceof ClSymbol)
    {
      ClSymbol symbol = (ClSymbol) element;
      final String name = symbol.getReferenceName();
      return name == null ? symbol.getText() : name;
    }
    if (element instanceof ClDef)
    {
      ClDef def = (ClDef) element;
      return def.getDefinedName();
    }
    return element.getText();
  }
}
