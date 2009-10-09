package org.jetbrains.plugins.scheme.findUsages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.lexer.SchemeFlexLexer;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;

/**
 * @author ilyas
 */
public class SchemeFindUsagesProvider implements FindUsagesProvider
{
  @Nullable
  public WordsScanner getWordsScanner()
  {
    System.out.println("getWordsScanner");
    return new DefaultWordsScanner(new SchemeFlexLexer(), Tokens.IDENTIFIERS, Tokens.COMMENTS, Tokens.STRINGS);
  }

  public boolean canFindUsagesFor(@NotNull PsiElement psiElement)
  {
    boolean ret = psiElement instanceof SchemeIdentifier;
    System.out.println("canFindUsagesFor " + System.identityHashCode(psiElement) + ": " + ret);
    return ret;
  }

  public String getHelpId(@NotNull PsiElement psiElement)
  {
    return null;
  }

  @NotNull
  public String getType(@NotNull PsiElement element)
  {
    if (element instanceof SchemeIdentifier)
    {
      return "symbol";
    }
//    if (element instanceof ClDef)
//    {
//      return "definition";
//    }
    return "entity";
  }

  @NotNull
  public String getDescriptiveName(@NotNull PsiElement element)
  {
    if (element instanceof SchemeIdentifier)
    {
      SchemeIdentifier symbol = (SchemeIdentifier) element;
      String name = symbol.getText();
      return name == null ? symbol.getText() : name;
    }

    return element.getText();
  }

  @NotNull
  public String getNodeText(@NotNull PsiElement element, boolean useFullName)
  {
    if (element instanceof SchemeIdentifier)
    {
      SchemeIdentifier symbol = (SchemeIdentifier) element;
      String name = symbol.getReferenceName();
      return name == null ? symbol.getText() : name;
    }

    return element.getText();
  }
}
