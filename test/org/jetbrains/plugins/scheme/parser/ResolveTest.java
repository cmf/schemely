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
    assert doesNotResolve(first(file, "x"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
  }

  @Test
  public void testBasicLambda()
  {
    PsiFile file = parse("(lambda (x) (+ x 3))");
    assert doesNotResolve(first(file, "x"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
  }

  @Test
  public void testLambda2Params()
  {
    PsiFile file = parse("(lambda (x y) (+ x y))");
    assert doesNotResolve(first(file, "x"));
    assert doesNotResolve(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testDottedLambdaParams()
  {
    PsiFile file = parse("(lambda (x . y) (+ x y))");
    assert doesNotResolve(first(file, "x"));
    assert doesNotResolve(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testListLambdaParams()
  {
    PsiFile file = parse("(lambda x (head x))");
    assert doesNotResolve(first(file, "x"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
  }

  @Test
  public void testBasicFunctionDefine()
  {
    PsiFile file = parse("(define f (lambda (x) (+ x 2))) (f 5)");
    assert doesNotResolve(first(file, "f"));
    assert resolvesTo(second(file, "f"), first(file, "f"));
  }

  @Test
  public void testBasicDefunStyle()
  {
    PsiFile file = parse("(define (f x) (+ x 2)) (f 5)");
    assert doesNotResolve(first(file, "f"));
    assert resolvesTo(second(file, "f"), first(file, "f"));
  }

  @Test
  public void testLambdaInternalDefine()
  {
    PsiFile file = parse("(lambda (x) (define y 4) (+ x y))");
    assert doesNotResolve(first(file, "x"));
    assert doesNotResolve(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testLambdaInternalDefineSeesParameters()
  {
    PsiFile file = parse("(lambda (x) (define y (+ 3 x)) (+ x y))");
    assert doesNotResolve(first(file, "x"));
    assert doesNotResolve(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testLambdaInternalDefineShadowsParameter()
  {
    PsiFile file = parse("(lambda (x) (define x 4) (+ x 3))");
    assert doesNotResolve(first(file, "x"));
    assert doesNotResolve(second(file, "x"));
    assert resolvesTo(third(file, "x"), second(file, "x"));
  }

  @Test
  public void testLambdaInternalDefineLetrecShadowing()
  {
    PsiFile file = parse("(lambda (x y) (define x 7) (define y x) (+ x y))");
    assert doesNotResolve(first(file, "x"));
    assert doesNotResolve(first(file, "y"));
    assert doesNotResolve(second(file, "x"));
    assert doesNotResolve(second(file, "y"));
    assert resolvesTo(third(file, "x"), second(file, "x"));
    assert resolvesTo(fourth(file, "x"), second(file, "x"));
    assert resolvesTo(third(file, "y"), second(file, "y"));
  }

  @Test
  public void testBasicLet()
  {
    PsiFile file = parse("(let ((x 2) (y 3)) (* x y))");
    assert doesNotResolve(first(file, "x"));
    assert doesNotResolve(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test
  public void testLetVarsDontSeeEachOther()
  {
    PsiFile file = parse("(lambda (x) (let ((x 7) (y (+ x 3))) (* y x)))");
    assert doesNotResolve(first(file, "x"));
    assert doesNotResolve(second(file, "x"));
    assert doesNotResolve(first(file, "y"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
    assert resolvesTo(fourth(file, "x"), second(file, "x"));
  }

  @Test
  public void testLetShadowing()
  {
    PsiFile file = parse("(let ((x 3)) (let ((x 5)) x))");
    assert doesNotResolve(first(file, "x"));
    assert doesNotResolve(second(file, "x"));
    assert resolvesTo(third(file, "x"), second(file, "x"));
  }
}
