package org.jetbrains.plugins.scheme.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.psi.impl.SchemePsiElementBase;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveResult;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveUtil;

import static org.jetbrains.plugins.scheme.psi.resolve.ResolveUtil.resolveFrom;

import java.util.*;


public class SchemeList extends SchemeListBase
{
  public static final String LAMBDA = "lambda";
  public static final String DEFINE = "define";
  public static final String LET = "let";
  public static final String LET_STAR = "let*";
  public static final String LETREC = "letrec";

  private static final String QUOTE = "quote";
  private static final String QUASIQUOTE = "quasiquote";
  private static final String UNQUOTE = "unquote";
  private static final String UNQUOTE_SPLICING = "unquote-splicing";

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
    if (isLambda())
    {
      return resolveFrom(place, processLambdaDeclaration(this, place));
    }
    else if (isDefinition())
    {
      return resolveFrom(place, processDefineDeclaration(this, place));
    }
    else if (isLet())
    {
      return resolveFrom(place, processLetDeclaration(this, place));
    }

    return ResolveResult.CONTINUE;
  }

  @Override
  public Collection<PsiElement> getSymbolVariants(SchemeIdentifier symbol)
  {
    String headText = getHeadText();
    if (headText == null)
    {
      return Collections.emptyList();
    }
    if (headText.equals(LAMBDA))
    {
      return new ArrayList<PsiElement>(processLambdaDeclaration(this, symbol));
    }
    else if (isDefinition())
    {
      return new ArrayList<PsiElement>(processDefineDeclaration(this, symbol));
    }
    else if (isLet())
    {
      return new ArrayList<PsiElement>(processLetDeclaration(this, symbol));
    }

    return Collections.emptyList();
  }

  @Override
  public int getQuotingLevel()
  {
    String headText = getHeadText();
    if (QUOTE.equals(headText) || QUASIQUOTE.equals(headText))
    {
      return 1;
    }
    else if (UNQUOTE.equals(headText) || UNQUOTE_SPLICING.equals(headText))
    {
      return -1;
    }
    return 0;
  }

  @NotNull
  private static List<SchemeIdentifier> processLambdaDeclaration(SchemeList lambda, SchemeIdentifier place)
  {
    if (!PsiTreeUtil.isAncestor(lambda, place, false))
    {
      return Collections.emptyList();
    }

    PsiElement formals = lambda.getSecondNonLeafElement();
    if (formals == null)
    {
      return Collections.emptyList();
    }

    // Lambda formals resolve to the symbol itself
    if (PsiTreeUtil.isAncestor(formals, place, false))
    {
      return Collections.singletonList(place);
    }

    List<SchemeIdentifier> ret = new ArrayList<SchemeIdentifier>();

    if (formals instanceof SchemeIdentifier)
    {
      // (lambda x (head x))
      if (place == formals)
      {
        return Collections.singletonList(place);
      }

      ret.add((SchemeIdentifier) formals);
    }
    else if (formals instanceof SchemeList)
    {
      // (lambda (x) (+ x 3))
      SchemeList args = (SchemeList) formals;

      ret.addAll(Arrays.asList(args.getAllIdentifiers()));
    }

    ret.addAll(processInternalDefinitions((SchemePsiElementBase) formals, place));

    return ret;
  }

  @NotNull
  public static List<SchemeIdentifier> processDefineDeclaration(SchemeList define, SchemeIdentifier place)
  {
    PsiElement formals = define.getSecondNonLeafElement();
    if (formals == null)
    {
      return Collections.emptyList();
    }

    // Define variables resolve to the symbol itself
    if ((PsiTreeUtil.isAncestor(formals, place, false)))
    {
      return Collections.singletonList(place);
    }

    PsiElement body = ResolveUtil.getNextNonLeafElement(formals);

    if ((PsiTreeUtil.findCommonParent(place, define) != define.getParent()) &&
        !PsiTreeUtil.isAncestor(body, place, false))
    {
      return Collections.emptyList();
    }

    if ((place != formals) && (formals instanceof SchemeIdentifier))
    {
      // (define x 3)
      SchemeIdentifier identifier = (SchemeIdentifier) formals;
      return Collections.singletonList(identifier);
    }
    else if (formals instanceof SchemeList)
    {
      // (define (plus3 x) (+ x 3))
      SchemeList args = (SchemeList) formals;
      return Arrays.asList(args.getAllIdentifiers());
    }

    return Collections.emptyList();
  }

  @NotNull
  private static List<SchemeIdentifier> processLetDeclaration(SchemeList declaration, SchemeIdentifier place)
  {
    if (!PsiTreeUtil.isAncestor(declaration, place, false))
    {
      return Collections.emptyList();
    }

    PsiElement vars = declaration.getSecondNonLeafElement();
    if (vars == null)
    {
      return Collections.emptyList();
    }

    List<SchemeIdentifier> ret = new ArrayList<SchemeIdentifier>();

    if ((PsiTreeUtil.isAncestor(vars, place, false)))
    {
      // place is either a let-bound variable or its value.
      PsiElement placeParent = place.getParent();
      if ((placeParent instanceof SchemeList) && (placeParent.getParent() == vars))
      {
        // If place is the first identifier in a list which is a sub-list of the let vars, it's a let-bound
        // variable so it only resolves to itself
        SchemeList parentList = (SchemeList) placeParent;
        if (place == parentList.getFirstIdentifier())
        {
          return Collections.singletonList(place);
        }
      }

      String style = declaration.getHeadText();
      if (style.equals(LET))
      {
        // It's part of a value for a let-bound variable, nothing in the let is in scope
        return Collections.emptyList();
      }
      else if (style.equals(LET_STAR))
      {
        if (vars instanceof SchemeList)
        {
          // (let ((x 3) (y 4)) (+ x y))
          SchemeList args = (SchemeList) vars;

          for (SchemeList binding : args.getSubLists())
          {
            SchemeIdentifier var = binding.getFirstIdentifier();
            if (var != null)
            {
              ret.add(var);
            }

            // Variables are only visible in later bindings
            if (PsiTreeUtil.isAncestor(binding, place, false))
            {
              return ret;
            }
          }
        }
        // Should never get this far
        return ret;
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
            SchemeIdentifier var = binding.getFirstIdentifier();
            if (var != null)
            {
              ret.add(var);
            }
          }
        }
        return ret;
      }
    }

    // TODO named let
    if (vars instanceof SchemeList)
    {
      // (let ((x 3) (y 4)) (+ x y))
      SchemeList args = (SchemeList) vars;
      for (SchemeList binding : args.getSubLists())
      {
        SchemeIdentifier var = binding.getFirstIdentifier();
        if (var != null)
        {
          ret.add(var);
        }
      }
    }

    // Process internal definitions first to get shadowing
    ret.addAll(processInternalDefinitions((SchemePsiElementBase) vars, place));

    return ret;
  }

  private static List<SchemeIdentifier> processInternalDefinitions(SchemePsiElementBase after, SchemeIdentifier place)
  {
    List<SchemeIdentifier> ret = new ArrayList<SchemeIdentifier>();

    PsiElement next = ResolveUtil.getNextNonLeafElement(after);
    while ((next != null) && isDefinition(next))
    {
      ret.addAll(processDefineDeclaration((SchemeList) next, place));

      // Don't process following definitions
      if (PsiTreeUtil.isAncestor(next, place, false))
      {
        break;
      }

      next = ResolveUtil.getNextNonLeafElement(next);
    }

    return ret;
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
    return list.isDefinition();
  }

  public static boolean isLet(PsiElement element)
  {
    if (!(element instanceof SchemeList))
    {
      return false;
    }

    SchemeList list = (SchemeList) element;
    return list.isLet();
  }

  public boolean isDefinition()
  {
    String headText = getHeadText();
    return DEFINE.equals(headText);
  }

  public boolean isLambda()
  {
    String headText = getHeadText();
    return LAMBDA.equals(headText);
  }

  private boolean isLet()
  {
    String headText = getHeadText();
    return LET.equals(headText) || LET_STAR.equals(headText) || LETREC.equals(headText);
  }
}
