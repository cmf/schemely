package org.jetbrains.plugins.scheme.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.plugins.scheme.psi.api.SchemeBraced;
import org.jetbrains.plugins.scheme.psi.impl.SchemePsiElementBase;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveResult;
import org.jetbrains.plugins.scheme.psi.resolve.ResolveUtil;

import static org.jetbrains.plugins.scheme.psi.resolve.ResolveUtil.resolveFrom;

import java.util.*;


public class SchemeList extends SchemeListBase implements SchemeBraced
{
  public static final String LAMBDA = "lambda";
  public static final String DEFINE = "define";
  public static final String DEFINE_SYNTAX = "define-syntax";
  public static final String DO = "do";
  public static final String LET = "let";
  public static final String LET_STAR = "let*";
  public static final String LETREC = "letrec";
  public static final String LET_SYNTAX = "let-syntax";
  public static final String LETREC_SYNTAX = "letrec-syntax";

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
  public PsiElement getFirstBrace()
  {
    PsiElement element = findChildByType(Tokens.LEFT_PAREN);
    assert element != null;
    return element;
  }

  public PsiElement getLastBrace()
  {
    return findChildByType(Tokens.RIGHT_PAREN);
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
    else if (isSyntaxDefinition())
    {
      return resolveFrom(place, processDefineSyntaxDeclaration(this, place));
    }
    else if (isLet())
    {
      return resolveFrom(place, processLetDeclaration(this, place));
    }
    else if (isDo())
    {
      return resolveFrom(place, processDoDeclaration(this, place));
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

      List<SchemeIdentifier> ret = new ArrayList<SchemeIdentifier>();
      if (PsiTreeUtil.isAncestor(body, place, false))
      {
        // Arguments are only visible in the define body
        ret.addAll(Arrays.asList(args.getAllIdentifiers()));
      }
      else
      {
        // Function name is visible everywhere
        ret.add(args.getFirstIdentifier());
      }

      return ret;
    }

    return Collections.emptyList();
  }

  @NotNull
  public static List<SchemeIdentifier> processDefineSyntaxDeclaration(SchemeList define, SchemeIdentifier place)
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

    if ((place != formals) && (formals instanceof SchemeIdentifier))
    {
      // (define-syntax x <whatever>)
      SchemeIdentifier identifier = (SchemeIdentifier) formals;
      return Collections.singletonList(identifier);
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

    String style = declaration.getHeadText();
    if (style == null)
    {
      return Collections.emptyList();
    }

    SchemeIdentifier namedLetName = null;
    if (vars instanceof SchemeIdentifier && style.equals(LET))
    {
      namedLetName = (SchemeIdentifier) vars;
      vars = ResolveUtil.getNextNonLeafElement(vars);

      // Named let name resolves to itself
      if (place == namedLetName)
      {
        return Collections.singletonList(place);
      }
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

      if (style.equals(LET) || style.equals(LET_SYNTAX))
      {
        // It's part of a value for a let-bound variable, nothing in the let is in scope
        return Collections.emptyList();
      }
      else if (style.equals(LET_STAR))
      {
        if (vars instanceof SchemeList)
        {
          // (let* ((x 3) (y 4)) (+ x y))
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
      else if (style.equals(LETREC) || style.equals(LETREC_SYNTAX))
      {
        if (vars instanceof SchemeList)
        {
          // (letrec ((x 3) (y 4)) (+ x y))
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

      if (namedLetName != null)
      {
        ret.add(namedLetName);
      }
    }

    // Process internal definitions first to get shadowing
    ret.addAll(processInternalDefinitions((SchemePsiElementBase) vars, place));

    return ret;
  }

  @NotNull
  private static List<SchemeIdentifier> processDoDeclaration(SchemeList declaration, SchemeIdentifier place)
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

    // Are we somewhere within the variable declarations?
    if ((PsiTreeUtil.isAncestor(vars, place, false)))
    {
      // place is either a let-bound variable, part of an init, or its value.
      PsiElement placeParent = place.getParent();
      if ((placeParent instanceof SchemeList) && (placeParent.getParent() == vars))
      {
        // If place is the first identifier in a list which is a sub-list of the do vars,
        // it's a do-bound variable so it only resolves to itself
        SchemeList parentList = (SchemeList) placeParent;
        if (place == parentList.getFirstIdentifier())
        {
          return Collections.singletonList(place);
        }

        // Init expressions are not in scope
        PsiElement init = parentList.getSecondNonLeafElement();
        if (init != null && PsiTreeUtil.isAncestor(init, place, false))
        {
          return Collections.emptyList();
        }
      }
    }

    // All vars are in scope everywhere else in the do
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

  public boolean isSyntaxDefinition()
  {
    String headText = getHeadText();
    return DEFINE_SYNTAX.equals(headText);
  }

  public boolean isLambda()
  {
    String headText = getHeadText();
    return LAMBDA.equals(headText);
  }

  private boolean isLet()
  {
    String headText = getHeadText();
    return LET.equals(headText) ||
           LET_STAR.equals(headText) ||
           LETREC.equals(headText) ||
           LET_SYNTAX.equals(headText) ||
           LETREC_SYNTAX.equals(headText);
  }

  public boolean isDo()
  {
    String headText = getHeadText();
    return DO.equals(headText);
  }
}
