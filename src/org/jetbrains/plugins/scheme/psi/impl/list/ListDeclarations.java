package org.jetbrains.plugins.scheme.psi.impl.list;

import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.psi.api.SchemeList;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveUtil;

/**
 * @author ilyas
 */
public class ListDeclarations
{
  public static final String LAMBDA = "lambda";
  public static final String DEFINE = "define";
  public static final String LET = "let";
  public static final String LET_STAR = "let*";
  public static final String LETREC = "letrec";

  public static boolean get(PsiScopeProcessor processor,
                            PsiElement lastParent,
                            PsiElement place,
                            SchemeList list,
                            @Nullable String headText)
  {
    if (headText == null)
    {
      return true;
    }
    if (headText.equals(LAMBDA))
    {
      return processLambdaDeclaration(processor, list, place, lastParent);
    }
    else if (isDefinitionText(headText))
    {
      return processDefineDeclaration(processor, list, place, lastParent);
    }
    else if (headText.equals(LET) || headText.equals(LET_STAR) || headText.equals(LETREC))
    {
      return processLetDeclaration(processor, list, place, lastParent, headText);
    }

    return true;
  }

  private static boolean processLambdaDeclaration(PsiScopeProcessor processor,
                                                  SchemeList lambda,
                                                  PsiElement place,
                                                  PsiElement lastParent)
  {
    if (PsiTreeUtil.findCommonParent(place, lambda) != lambda)
    {
      return true;
    }

    PsiElement formals = lambda.getSecondNonLeafElement();
    if (formals == null)
    {
      return true;
    }

    // Lambda formals are not references
    if (PsiTreeUtil.findCommonParent(place, formals) == formals)
    {
      return false;
    }

    // Process internal definitions first to get shadowing
    if (!processInternalDefinitions(processor, formals, place, lastParent))
    {
      return false;
    }

    if (formals instanceof SchemeIdentifier)
    {
      // (lambda x (head x))
      if ((place != formals) && !ResolveUtil.processElement(processor, (SchemeIdentifier) formals))
      {
        return false;
      }
    }
    else if (formals instanceof SchemeList)
    {
      // (lambda (x) (+ x 3))
      SchemeList args = (SchemeList) formals;

      for (SchemeIdentifier arg : args.getAllSymbols())
      {
        if (!ResolveUtil.processElement(processor, arg))
        {
          return false;
        }
      }

      return true;
    }

    return true;
  }

  private static boolean processDefineDeclaration(PsiScopeProcessor processor,
                                                  SchemeList define,
                                                  PsiElement place,
                                                  PsiElement lastParent)
  {
    PsiElement formals = define.getSecondNonLeafElement();
    if (formals == null)
    {
      return true;
    }

    // Define variables are not references
    if ((PsiTreeUtil.findCommonParent(place, formals) == formals))
    {
      return false;
    }

    PsiElement body = ResolveUtil.getNextNonLeafElement(formals);

    if ((PsiTreeUtil.findCommonParent(place, define) != define.getParent()) &&
        (PsiTreeUtil.findCommonParent(place, body) != body))
    {
      return true;
    }

    if (formals instanceof SchemeIdentifier)
    {
      // (define x 3)
      if ((place != formals) && !ResolveUtil.processElement(processor, (SchemeIdentifier) formals))
      {
        return false;
      }
    }
    else if (formals instanceof SchemeList)
    {
      // (define (plus3 x) (+ x 3))
      SchemeList args = (SchemeList) formals;

      for (SchemeIdentifier arg : args.getAllSymbols())
      {
        if (!ResolveUtil.processElement(processor, arg))
        {
          return false;
        }
      }

      return true;
    }

    return true;
  }

  private static boolean processLetDeclaration(PsiScopeProcessor processor,
                                               SchemeList declaration,
                                               PsiElement place,
                                               PsiElement lastParent,
                                               String style)
  {
    if (PsiTreeUtil.findCommonParent(place, declaration) != declaration)
    {
      return true;
    }

    PsiElement vars = declaration.getSecondNonLeafElement();
    if (vars == null)
    {
      return true;
    }

    if ((PsiTreeUtil.findCommonParent(place, vars) == vars))
    {
      // place is either a let-bound variable or its value.
      PsiElement placeParent = place.getParent();
      if ((placeParent instanceof SchemeList) && (placeParent.getParent() == vars))
      {
        // If place is the first identifier in a list which is a sub-list of the let vars, it's a let-bound variable
        // so it's not a reference
        SchemeList parentList = (SchemeList) placeParent;
        if (place == parentList.getFirstIdentifier())
        {
          return false;
        }
      }

      if (style.equals(LET))
      {
        // It's part of a value for a let-bound variable, nothing in the let is in scope but we should
        // keep searching
        return true;
      }
      else if (style.equals(LET_STAR))
      {
        if (vars instanceof SchemeList)
        {
          // (let ((x 3) (y 4)) (+ x y))
          SchemeList args = (SchemeList) vars;
          SchemeList[] bindings = args.getSubLists();

          // Skip later bindings
          int i = bindings.length - 1;
          while ((i >= 0) && !PsiTreeUtil.isAncestor(bindings[i], place, true))
          {
            i--;
          }
          // bindings[i] is now our containing binding list - skip it
          i--;

          // process all remaining bindings
          while (i >= 0)
          {
            if (!processFirstIdentifier(processor, bindings[i]))
            {
              return false;
            }
            i--;
          }
        }
        return true;
      }
      else if (style.equals(LETREC))
      {
        if (vars instanceof SchemeList)
        {
          // (let ((x 3) (y 4)) (+ x y))
          SchemeList args = (SchemeList) vars;
          SchemeList[] bindings = args.getSubLists();
          for (SchemeList binding : bindings)
          {
            if (!processFirstIdentifier(processor, binding))
            {
              return false;
            }
          }
        }
        return true;
      }
    }

    // Process internal definitions first to get shadowing
    if (!processInternalDefinitions(processor, vars, place, lastParent))
    {
      return false;
    }

    // TODO named let
    if (vars instanceof SchemeList)
    {
      // (let ((x 3) (y 4)) (+ x y))
      SchemeList args = (SchemeList) vars;

      for (SchemeList arg : args.getSubLists())
      {
        if (!processFirstIdentifier(processor, arg))
        {
          return false;
        }
      }
    }

    return true;
  }

  private static boolean processFirstIdentifier(PsiScopeProcessor processor, SchemeList arg)
  {
    SchemeIdentifier firstIdentifier = arg.getFirstIdentifier();
    if (firstIdentifier != null)
    {
      if (!ResolveUtil.processElement(processor, firstIdentifier))
      {
        return false;
      }
    }
    return true;
  }

  private static boolean processInternalDefinitions(PsiScopeProcessor processor,
                                                    PsiElement second,
                                                    PsiElement place,
                                                    PsiElement lastParent)
  {
    PsiElement next = ResolveUtil.getNextNonLeafElement(second);
    while ((next != null) && isDefinition(next))
    {
      if (!processDefineDeclaration(processor, (SchemeList) next, place, lastParent))
      {
        return false;
      }
      next = ResolveUtil.getNextNonLeafElement(next);
    }
    return true;
  }

  public static boolean isLocal(PsiElement element)
  {
    if (element instanceof SchemeIdentifier)
    {
      SchemeIdentifier symbol = (SchemeIdentifier) element;
      PsiElement parent = symbol.getParent();

      if (isDefinition(parent))
      {
        return true;
      }
      else if (parent != null)
      {
        PsiElement grandparent = parent.getParent();
        if (isDefinition(grandparent))
        {
          return true;
        }
        else if (grandparent != null)
        {
          PsiElement greatGrandparent = grandparent.getParent();
          if (isLet(greatGrandparent))
          {
            return true;
          }
        }
      }
    }

    return false;
  }

  public static boolean isDefinition(PsiElement element)
  {
    // Handles null
    if (!(element instanceof SchemeList))
    {
      return false;
    }

    SchemeList list = (SchemeList) element;
    String head = list.getHeadText();
    return isDefinitionText(head);
  }

  private static boolean isDefinitionText(String headText)
  {
    return DEFINE.equals(headText) || LAMBDA.equals(headText);
  }

  public static boolean isLet(PsiElement element)
  {
    if (!(element instanceof SchemeList))
    {
      return false;
    }

    SchemeList list = (SchemeList) element;
    String head = list.getHeadText();
    return isLetText(head);
  }

  private static boolean isLetText(String headText)
  {
    return LET.equals(headText) || LET_STAR.equals(headText) || LETREC.equals(headText);
  }
}
