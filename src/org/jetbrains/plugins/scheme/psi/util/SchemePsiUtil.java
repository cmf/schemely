package org.jetbrains.plugins.scheme.psi.util;

import com.intellij.psi.PsiElement;


public class SchemePsiUtil
{
  public static <T> T findNextSiblingByClass(PsiElement element, Class<T> aClass)
  {
    PsiElement next = element.getNextSibling();
    while (next != null && !aClass.isInstance(next))
    {
      next = next.getNextSibling();
    }
    return (T) next;
  }

  //  public static ClKeyImpl findNamespaceKeyByName(SchemeList ns, String keyName)
  //  {
  //    final SchemeList list = ns.findFirstChildByClass(SchemeList.class);
  //    if (list == null)
  //    {
  //      return null;
  //    }
  //    for (PsiElement element : list.getChildren())
  //    {
  //      if (element instanceof ClKeyImpl)
  //      {
  //        ClKeyImpl key = (ClKeyImpl) element;
  //        if (keyName.equals(key.getText()))
  //        {
  //          return key;
  //        }
  //      }
  //    }
  //    return null;
  //  }

}
