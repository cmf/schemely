package org.jetbrains.plugins.scheme.psi.impl.list;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.psi.SchemePsiElement;
import org.jetbrains.plugins.scheme.psi.api.SchemeAbbreviation;
import org.jetbrains.plugins.scheme.psi.api.SchemeList;
import org.jetbrains.plugins.scheme.psi.api.SchemeVector;
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
    else if (headText.equals(LET))
    {
      return processLetDeclaration(processor, list, place, lastParent);
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

    PsiElement second = lambda.getSecondNonLeafElement();
    if (second == null)
    {
      return true;
    }

    // Lambda formals are not references
    if (PsiTreeUtil.findCommonParent(place, second) == second)
    {
      return false;
    }

    // Process internal definitions first to get shadowing
    if (!processInternalDefinitions(processor, second, place, lastParent))
    {
      return false;
    }

    if (second instanceof SchemeIdentifier)
    {
      // (lambda x (head x))
      if ((place != second) && !ResolveUtil.processElement(processor, (SchemeIdentifier) second))
      {
        return false;
      }
    }
    else if (second instanceof SchemeList)
    {
      // (lambda (x) (+ x 3))
      if (PsiTreeUtil.findCommonParent(place, lambda) == lambda)
      {
        SchemeList args = (SchemeList) second;

        for (SchemeIdentifier arg : args.getAllSymbols())
        {
          if (!ResolveUtil.processElement(processor, arg))
          {
            return false;
          }
        }

        return true;
      }
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

    PsiElement body = ResolveUtil.getNextNonLeafElement(formals);
    
    if ((PsiTreeUtil.findCommonParent(place, define) != define.getParent()) &&
        (PsiTreeUtil.findCommonParent(place, body) != body))
    {
      return true;
    }

    // Define variables are not references
    if ((PsiTreeUtil.findCommonParent(place, formals) == formals))
    {
      return false;
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
      if (PsiTreeUtil.findCommonParent(place, define) == define)
      {
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
    }

    return true;
  }

  private static boolean processLetDeclaration(PsiScopeProcessor processor,
                                               SchemeList declaration,
                                               PsiElement place,
                                               PsiElement lastParent)
  {
    if (PsiTreeUtil.findCommonParent(place, declaration) != declaration)
    {
      return true;
    }

    PsiElement second = declaration.getSecondNonLeafElement();
    if (second == null)
    {
      return true;
    }

    if ((PsiTreeUtil.findCommonParent(place, second) == second))
    {
      // place is either a let-bound variable or its value.
      PsiElement placeParent = place.getParent();
      if ((placeParent instanceof SchemeList) && (placeParent.getParent() == second))
      {
        // If place is the first identifier in a list which is a sub-list of the let vars, it's a let-bound variable
        // so it's not a reference
        SchemeList parentList = (SchemeList) placeParent;
        if (place == parentList.getFirstIdentifier())
        {
          return false;
        }
      }

      // It's part of a value for a let-bound variable, nothing in the let is in scope but we should
      // keep searching
      return true;
    }

    // Process internal definitions first to get shadowing
    if (!processInternalDefinitions(processor, second, place, lastParent))
    {
      return false;
    }

    // TODO named let
    //    if (second instanceof SchemeIdentifier)
    //    {
    //      // (define x 3)
    //      if ((place != second) && !ResolveUtil.processElement(processor, (SchemeIdentifier) second))
    //      {
    //        return false;
    //      }
    //    }
    //    else
    if (second instanceof SchemeList)
    {
      // (let ((x 3) (y 4)) (+ x y))
      SchemeList args = (SchemeList) second;

      for (SchemeList arg : args.getSubLists())
      {
        SchemeIdentifier firstIdentifier = arg.getFirstIdentifier();
        if (firstIdentifier != null)
        {
          if (!ResolveUtil.processElement(processor, firstIdentifier))
          {
            return false;
          }
        }
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

  private static boolean processDeclareDeclaration(PsiScopeProcessor processor,
                                                   SchemeList list,
                                                   PsiElement place,
                                                   PsiElement lastParent)
  {
    SchemeVector paramVector = list.findFirstChildByClass(SchemeVector.class);
    if (paramVector != null)
    {
      for (SchemeIdentifier symbol : paramVector.getOddSymbols())
      {
        if (!ResolveUtil.processElement(processor, symbol))
        {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean processLoopDeclaration(PsiScopeProcessor processor,
                                                SchemeList list,
                                                PsiElement place,
                                                PsiElement lastParent)
  {
    if (lastParent != null && lastParent.getParent() == list)
    {
      SchemeVector paramVector = list.findFirstChildByClass(SchemeVector.class);
      if (paramVector != null)
      {
        for (SchemeIdentifier symbol : paramVector.getOddSymbols())
        {
          if (!ResolveUtil.processElement(processor, symbol))
          {
            return false;
          }
        }
      }
      return true;
    }
    return true;
  }

  private static boolean processImportDeclaration(PsiScopeProcessor processor, SchemeList list, PsiElement place)
  {
    PsiElement[] children = list.getChildren();
    Project project = list.getProject();
    JavaPsiFacade facade = JavaPsiFacade.getInstance(project);

    for (PsiElement child : children)
    {
      if (child instanceof SchemeIdentifier)
      {
        SchemeIdentifier symbol = (SchemeIdentifier) child;
        String symbolName = symbol.getNameString();
        PsiClass clazz = facade.findClass(symbolName, GlobalSearchScope.allScope(project));
        if (clazz != null && !ResolveUtil.processElement(processor, clazz))
        {
          return false;
        }
      }
      else if (child instanceof SchemeAbbreviation)
      {
        // process import of form (import '(java.util List Set))
        SchemeAbbreviation abbreviation = (SchemeAbbreviation) child;
        SchemePsiElement element = abbreviation.getQuotedElement();
        if (element instanceof SchemeList)
        {
          SchemeList inner = (SchemeList) element;
          PsiElement first = inner.getFirstNonLeafElement();
          if (first instanceof SchemeIdentifier)
          {
            SchemeIdentifier packSym = (SchemeIdentifier) first;

            PsiPackage pack = facade.findPackage(packSym.getNameString());
            if (pack != null)
            {
              if (place.getParent() == inner && place != packSym)
              {
                pack.processDeclarations(processor, ResolveState.initial(), null, place);
              }
              else
              {
                PsiElement next = packSym.getNextSibling();
                while (next != null)
                {
                  if (next instanceof SchemeIdentifier)
                  {
                    SchemeIdentifier clazzSym = (SchemeIdentifier) next;
                    PsiClass
                      clazz =
                      facade.findClass(pack.getQualifiedName() + "." + clazzSym.getNameString(),
                                       GlobalSearchScope.allScope(project));
                    if (clazz != null && !ResolveUtil.processElement(processor, clazz))
                    {
                      return false;
                    }
                  }
                  next = next.getNextSibling();
                }
              }
            }
          }
        }
      }

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
