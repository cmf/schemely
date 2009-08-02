package org.jetbrains.plugins.scheme.psi.impl.symbols;

import org.jetbrains.plugins.scheme.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.scheme.psi.api.ClList;

import javax.swing.*;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static org.jetbrains.plugins.scheme.psi.impl.list.ListDeclarations.*;
import org.jetbrains.plugins.scheme.SchemeIcons;

/**
 * @author ilyas
 */
public class SymbolUtils
{

  public static Icon getIcon(ClSymbol symbol, int flags)
  {
    final PsiElement parent = PsiTreeUtil.getParentOfType(symbol, ClList.class);
    if (parent instanceof ClList)
    {
      ClList list = (ClList) parent;

      // Functions and defs
      if (symbol == list.getSecondNonLeafElement())
      {
        final PsiElement fst = list.getFirstNonLeafElement();
        if (fst instanceof ClSymbol)
        {
          ClSymbol lstSym = (ClSymbol) fst;
          final String nameString = lstSym.getNameString();

          if (FN.equals(nameString))
          {
            return SchemeIcons.FUNCTION;
          }
          if (DEFN.equals(nameString))
          {
            return SchemeIcons.FUNCTION;
          }
        }
      }


    }

    return null;
  }
}
