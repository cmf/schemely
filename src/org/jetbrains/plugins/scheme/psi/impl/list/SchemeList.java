package org.jetbrains.plugins.scheme.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.psi.impl.SchemePsiElementBase;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveResult;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveUtil;


public class SchemeList extends SchemeListBase
{
  public static final String LAMBDA = "lambda";
  public static final String DEFINE = "define";
  public static final String LET = "let";
  public static final String LET_STAR = "let*";
  public static final String LETREC = "letrec";

  public SchemeList(@NotNull ASTNode astNode)
  {
    super(astNode, "SchemeList");
  }

  @Override
  public String toString()
  {
    return getText();
  }

  @NotNull
  @Override
  public ResolveResult resolve(SchemeIdentifier place)
  {
    String headText = getHeadText();
    if (headText == null)
    {
      return ResolveResult.CONTINUE;
    }
    if (headText.equals(LAMBDA))
    {
      return processLambdaDeclaration(this, place);
    }
    else if (isDefinitionText(headText))
    {
      return processDefineDeclaration(this, place);
    }
    else if (headText.equals(LET) || headText.equals(LET_STAR) || headText.equals(LETREC))
    {
      return processLetDeclaration(this, place, headText);
    }

    return ResolveResult.CONTINUE;
  }

  @NotNull
  private static ResolveResult processLambdaDeclaration(SchemeList lambda, SchemeIdentifier place)
  {
    if (PsiTreeUtil.findCommonParent(place, lambda) != lambda)
    {
      return ResolveResult.CONTINUE;
    }

    PsiElement formals = lambda.getSecondNonLeafElement();
    if (formals == null)
    {
      return ResolveResult.CONTINUE;
    }

    // Lambda formals are not references
    if (PsiTreeUtil.findCommonParent(place, formals) == formals)
    {
      return ResolveResult.NONE;
    }

    // Process internal definitions first to get shadowing
    ResolveResult identifier = processInternalDefinitions((SchemePsiElementBase) formals, place);
    if (identifier.isDone())
    {
      return identifier;
    }

    if (formals instanceof SchemeIdentifier)
    {
      // (lambda x (head x))
      if (place == formals)
      {
        return ResolveResult.NONE;
      }

      SchemeIdentifier arg = (SchemeIdentifier) formals;
      if (place.couldReference(arg))
      {
        return ResolveResult.of(arg);
      }
    }
    else if (formals instanceof SchemeList)
    {
      // (lambda (x) (+ x 3))
      SchemeList args = (SchemeList) formals;

      for (SchemeIdentifier arg : args.getAllSymbols())
      {
        if (place.couldReference(arg))
        {
          return ResolveResult.of(arg);
        }
      }
    }

    return ResolveResult.CONTINUE;
  }

  @NotNull
  public static ResolveResult processDefineDeclaration(SchemeList define, SchemeIdentifier place)
  {
    PsiElement formals = define.getSecondNonLeafElement();
    if (formals == null)
    {
      return ResolveResult.CONTINUE;
    }

    // Define variables are not references
    if ((PsiTreeUtil.findCommonParent(place, formals) == formals))
    {
      // TODO check this
      return ResolveResult.of(place);
    }

    PsiElement body = ResolveUtil.getNextNonLeafElement(formals);

    if ((PsiTreeUtil.findCommonParent(place, define) != define.getParent()) &&
        (PsiTreeUtil.findCommonParent(place, body) != body))
    {
      return ResolveResult.CONTINUE;
    }

    if ((place != formals) && (formals instanceof SchemeIdentifier))
    {
      SchemeIdentifier identifier = (SchemeIdentifier) formals;

      // (define x 3)
      if (place.couldReference(identifier))
      {
        return ResolveResult.of(identifier);
      }
    }
    else if (formals instanceof SchemeList)
    {
      // (define (plus3 x) (+ x 3))
      SchemeList args = (SchemeList) formals;

      for (SchemeIdentifier arg : args.getAllSymbols())
      {
        if (place.couldReference(arg))
        {
          return ResolveResult.of(arg);
        }
      }
    }

    return ResolveResult.CONTINUE;
  }

  @NotNull
  private static ResolveResult processLetDeclaration(SchemeList declaration, SchemeIdentifier place, String style)
  {
    if (PsiTreeUtil.findCommonParent(place, declaration) != declaration)
    {
      return ResolveResult.CONTINUE;
    }

    PsiElement vars = declaration.getSecondNonLeafElement();
    if (vars == null)
    {
      return ResolveResult.CONTINUE;
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
          return ResolveResult.NONE;
        }
      }

      if (style.equals(LET))
      {
        // It's part of a value for a let-bound variable, nothing in the let is in scope but we should
        // keep searching
        return ResolveResult.CONTINUE;
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
            ResolveResult result = processFirstIdentifier(bindings[i], place);
            if (result.isDone())
            {
              return result;
            }
            i--;
          }
        }
        return ResolveResult.CONTINUE;
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
            ResolveResult result = processFirstIdentifier(binding, place);
            if (result.isDone())
            {
              return result;
            }
          }
        }
        return ResolveResult.CONTINUE;
      }
    }

    // Process internal definitions first to get shadowing
    ResolveResult result = processInternalDefinitions((SchemePsiElementBase) vars, place);
    if (result.isDone())
    {
      return result;
    }

    // TODO named let
    if (vars instanceof SchemeList)
    {
      // (let ((x 3) (y 4)) (+ x y))
      SchemeList args = (SchemeList) vars;

      for (SchemeList arg : args.getSubLists())
      {
        result = processFirstIdentifier(arg, place);
        if (result.isDone())
        {
          return result;
        }
      }
    }

    return ResolveResult.CONTINUE;
  }

  @NotNull
  private static ResolveResult processFirstIdentifier(SchemeList arg, SchemeIdentifier place)
  {
    SchemeIdentifier firstIdentifier = arg.getFirstIdentifier();
    if ((firstIdentifier != null) && place.couldReference(firstIdentifier))
    {
      return ResolveResult.of(firstIdentifier);
    }
    return ResolveResult.CONTINUE;
  }

  private static ResolveResult processInternalDefinitions(SchemePsiElementBase after, SchemeIdentifier place)
  {
    PsiElement next = ResolveUtil.getNextNonLeafElement(after);
    while ((next != null) && isDefinition(next))
    {
      ResolveResult result = processDefineDeclaration((SchemeList) next, place);
      if (result.isDone())
      {
        return result;
      }
      next = ResolveUtil.getNextNonLeafElement(next);
    }
    return ResolveResult.CONTINUE;
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
