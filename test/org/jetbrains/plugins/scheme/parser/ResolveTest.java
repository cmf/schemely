package org.jetbrains.plugins.scheme.parser;

import org.testng.annotations.Test;
import com.intellij.psi.PsiFile;

/**
 * @author Colin Fleming
 */
public class ResolveTest extends ParserTestBase
{
  @Test
  public void testBasicDefine()
  {
    PsiFile file = parse("(define x 3) x");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
  }

  @Test
  public void testBasicLambda()
  {
    PsiFile file = parse("(lambda (x) (+ x 3))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
  }

  @Test
  public void testLambda2Params()
  {
    PsiFile file = parse("(lambda (x y) (+ x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testDottedLambdaParams()
  {
    PsiFile file = parse("(lambda (x . y) (+ x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testListLambdaParams()
  {
    PsiFile file = parse("(lambda x (head x))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
  }

  @Test
  public void testBasicFunctionDefine()
  {
    PsiFile file = parse("(define f (lambda (x) (+ x 2))) (f 5)");
    assert resolvesToSelf(first(file, "f"));
    assert resolvesTo(second(file, "f"), first(file, "f"));
  }

  @Test
  public void testBasicDefunStyle()
  {
    PsiFile file = parse("(define (f x) (+ x 2)) (f 5)");
    assert resolvesToSelf(first(file, "f"));
    assert resolvesTo(second(file, "f"), first(file, "f"));
  }

  @Test
  public void testLambdaInternalDefine()
  {
    PsiFile file = parse("(lambda (x) (define y 4) (+ x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testLambdaInternalDefineSeesParameters()
  {
    PsiFile file = parse("(lambda (x) (define y (+ 3 x)) (+ x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testLambdaInternalDefineShadowsParameter()
  {
    PsiFile file = parse("(lambda (x) (define x 4) (+ x 3))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(second(file, "x"));
    assert resolvesTo(third(file, "x"), second(file, "x"));
  }

  @Test
  public void testLambdaInternalDefineLetrecShadowing()
  {
    PsiFile file = parse("(lambda (x y) (define x 7) (define y x) (+ x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesToSelf(second(file, "x"));
    assert resolvesToSelf(second(file, "y"));
    assert resolvesTo(third(file, "x"), second(file, "x"));
    assert resolvesTo(fourth(file, "x"), second(file, "x"));
    assert resolvesTo(third(file, "y"), second(file, "y"));
  }

  @Test
  public void testBasicLet()
  {
    PsiFile file = parse("(let ((x 2) (y 3)) (* x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testLetVarsDontSeeEachOther()
  {
    PsiFile file = parse("(lambda (x) (let ((x 7) (y (+ x 3))) (* y x)))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(second(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
    assert resolvesTo(fourth(file, "x"), second(file, "x"));
  }

  @Test
  public void testLetShadowing()
  {
    PsiFile file = parse("(let ((x 3)) (let ((x 5)) x))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(second(file, "x"));
    assert resolvesTo(third(file, "x"), second(file, "x"));
  }

  @Test
  public void testEmbeddedLet()
  {
    PsiFile file = parse("(let ((x (let ((y 5)) y)) (y 3)) (* x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
    assert resolvesToSelf(third(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(fourth(file, "y"), third(file, "y"));
  }

  @Test
  public void testBasicLetStar()
  {
    PsiFile file = parse("(let* ((x 2) (y 3)) (* x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testLetStarSeesPreviousBindings()
  {
    PsiFile file = parse("(let* ((x 2) (y x)) (* x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testLetStarDoesntSeeLaterBindings()
  {
    PsiFile file = parse("(let* ((x y) (y 3)) (* x y))");
    assert resolvesToSelf(first(file, "x"));
    assert doesNotResolve(first(file, "y"));
    assert resolvesToSelf(second(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "y"), second(file, "y"));
  }


  @Test
  public void testBasicLetRec()
  {
    PsiFile file = parse("(letrec ((x 2) (y 3)) (* x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testLetRecSeesPreviousBindings()
  {
    PsiFile file = parse("(letrec ((x 2) (y x)) (* x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testLetRecSeesLaterBindings()
  {
    PsiFile file = parse("(letrec ((x y) (y 3)) (* x y))");
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(second(file, "y"));
    assert resolvesTo(first(file, "y"), second(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "y"), second(file, "y"));
  }
}
