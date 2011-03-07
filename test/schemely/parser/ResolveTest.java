package schemely.parser;

import org.intellij.lang.annotations.Language;
import org.testng.annotations.Test;
import com.intellij.psi.PsiFile;

/**
 * @author Colin Fleming
 */
public class ResolveTest extends ParserTestBase
{
  @Test(groups = "Schemely")
  public void testBasicDefine()
  {
    @Language("Scheme") String contents = "(define x 3) x";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
  }

  @Test(groups = "Schemely")
  public void testBasicLambda()
  {
    @Language("Scheme") String contents = "(lambda (x) (+ x 3)) x";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert !resolvesTo(third(file, "x"), first(file, "x"));
  }

  @Test(groups = "Schemely")
  public void testLambda2Params()
  {
    @Language("Scheme") String contents = "(lambda (x y) (+ x y)) x y";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
    assert !resolvesTo(third(file, "x"), first(file, "x"));
    assert !resolvesTo(third(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testDottedLambdaParams()
  {
    @Language("Scheme") String contents = "(lambda (x . y) (+ x y)) x y";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
    assert !resolvesTo(third(file, "x"), first(file, "x"));
    assert !resolvesTo(third(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testListLambdaParams()
  {
    @Language("Scheme") String contents = "(lambda x (head x)) x";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert !resolvesTo(third(file, "x"), first(file, "x"));
  }

  @Test(groups = "Schemely")
  public void testBasicFunctionDefine()
  {
    @Language("Scheme") String contents = "(define f (lambda (x) (+ x 2))) (f 5)";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "f"));
    assert resolvesTo(second(file, "f"), first(file, "f"));
  }

  @Test(groups = "Schemely")
  public void testBasicDefunStyle()
  {
    @Language("Scheme") String contents = "(define (f x) (+ x 2)) (f 5) x";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "f"));
    assert resolvesTo(second(file, "f"), first(file, "f"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert !resolvesTo(third(file, "x"), first(file, "x"));
  }

  @Test(groups = "Schemely")
  public void testDefunReturnArgument()
  {
    @Language("Scheme") String contents = "(define (f x y) x y) (f 5) x";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "f"));
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "f"), first(file, "f"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
    assert !resolvesTo(third(file, "x"), first(file, "x"));
  }

  @Test(groups = "Schemely")
  public void testDefunNoArgs()
  {
    @Language("Scheme") String contents = "(define (f) 5) (f)";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "f"));
    assert resolvesTo(second(file, "f"), first(file, "f"));
  }

  @Test(groups = "Schemely")
  public void testLambdaInternalDefine()
  {
    @Language("Scheme") String contents = "(lambda (x) (define y 4) (+ x y)) y";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
    assert !resolvesTo(third(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testLambdaInternalDefineSeesParameters()
  {
    @Language("Scheme") String contents = "(lambda (x) (define y (+ 3 x)) (+ x y))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testLambdaInternalDefineShadowsParameter()
  {
    @Language("Scheme") String contents = "(lambda (x) (define x 4) (+ x 3))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(second(file, "x"));
    assert resolvesTo(third(file, "x"), second(file, "x"));
  }

  @Test(groups = "Schemely")
  public void testLambdaInternalDefineLetrecShadowing()
  {
    @Language("Scheme") String contents = "(lambda (x y) (define x 7) (define y x) (+ x y))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesToSelf(second(file, "x"));
    assert resolvesToSelf(second(file, "y"));
    assert resolvesTo(third(file, "x"), second(file, "x"));
    assert resolvesTo(fourth(file, "x"), second(file, "x"));
    assert resolvesTo(third(file, "y"), second(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testBasicLet()
  {
    @Language("Scheme") String contents = "(let ((x 2) (y 3)) (* x y))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testLetVarsDontSeeEachOther()
  {
    @Language("Scheme") String contents = "(lambda (x) (let ((x 7) (y (+ x 3))) (* y x)))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(second(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
    assert resolvesTo(fourth(file, "x"), second(file, "x"));
  }

  @Test(groups = "Schemely")
  public void testLetShadowing()
  {
    @Language("Scheme") String contents = "(let ((x 3)) (let ((x 5)) x))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(second(file, "x"));
    assert resolvesTo(third(file, "x"), second(file, "x"));
  }

  @Test(groups = "Schemely")
  public void testLetInternalDefineShadowing()
  {
    @Language("Scheme") String contents = "(let ((x 3)) (define x x) (define x x) x)";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(second(file, "x"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesToSelf(fourth(file, "x"));
    assert resolvesTo(fifth(file, "x"), second(file, "x"));
    assert resolvesTo(sixth(file, "x"), fourth(file, "x"));
  }

  @Test(groups = "Schemely")
  public void testEmbeddedLet()
  {
    @Language("Scheme") String contents = "(let ((x (let ((y 5)) y)) (y 3)) (* x y))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
    assert resolvesToSelf(third(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(fourth(file, "y"), third(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testBasicLetStar()
  {
    @Language("Scheme") String contents = "(let* ((x 2) (y 3)) (* x y))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testLetStarSeesPreviousBindings()
  {
    @Language("Scheme") String contents = "(let* ((x 2) (y x)) (* x y))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testLetStarDoesntSeeLaterBindings()
  {
    @Language("Scheme") String contents = "(let* ((x y) (y 3)) (* x y))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert doesNotResolve(first(file, "y"));
    assert resolvesToSelf(second(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "y"), second(file, "y"));
  }


  @Test(groups = "Schemely")
  public void testBasicLetRec()
  {
    @Language("Scheme") String contents = "(letrec ((x 2) (y 3)) (* x y))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testLetRecSeesPreviousBindings()
  {
    @Language("Scheme") String contents = "(letrec ((x 2) (y x)) (* x y))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testLetRecSeesLaterBindings()
  {
    @Language("Scheme") String contents = "(letrec ((x y) (y 3)) (* x y))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(second(file, "y"));
    assert resolvesTo(first(file, "y"), second(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "y"), second(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testBasicNamedLet()
  {
    @Language("Scheme") String contents = "(let loop ((x 2) (y 3)) (loop (+ x 1) (+ y 1)))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "loop"));
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "loop"), first(file, "loop"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testDo()
  {
    @Language("Scheme") String contents = "(do ((x 2) (y 3 (+ y 1))) ((= x y) y) x y)";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(first(file, "y"));
    assert resolvesTo(second(file, "x"), first(file, "x"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(second(file, "y"), first(file, "y"));
    assert resolvesTo(third(file, "y"), first(file, "y"));
    assert resolvesTo(fourth(file, "y"), first(file, "y"));
    assert resolvesTo(fifth(file, "y"), first(file, "y"));
  }

  @Test(groups = "Schemely")
  public void testDoInitDoesntResolve()
  {
    @Language("Scheme") String contents = "(let ((x 2)) (do ((x x)) ((= x 3) x) x))";
    setUp();
    PsiFile file = parse(contents);
    assert resolvesToSelf(first(file, "x"));
    assert resolvesToSelf(second(file, "x"));
    assert resolvesTo(third(file, "x"), first(file, "x"));
    assert resolvesTo(fourth(file, "x"), second(file, "x"));
    assert resolvesTo(fifth(file, "x"), second(file, "x"));
  }
}
