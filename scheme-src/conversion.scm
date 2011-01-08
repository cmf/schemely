(define-alias StdLanguages com.intellij.lang.StdLanguages)
(define-alias PsiElement com.intellij.psi.PsiElement)

(define-syntax type-match
  (syntax-rules (else)
    ((type-match var (else result1 ...))
     (begin result1 ...))
    ((type-match var (type new-var)) #f)
    ((type-match var ((type new-var)) clause1 ...)
     (or (instance? var type) (type-match var clause1 ...)))
    ((type-match var ((type new-var) result1 ...))
     (if (instance? var type)
         (let ((new-var :: type (as type var))) result1 ...)))
    ((type-match var ((type new-var) result1 ...)
                 clause1 ...)
     (if (instance? var type)
         (let ((new-var :: type (as type var))) result1 ...)
         (type-match var clause1 ...)))))

(define psi-to-text
  (lambda (element :: PsiElement)
    (cond
      ((eq? element #!null) "")
      ((not (eq? (element:get-language) StdLanguages:.JAVA)) "")
      (else
        ; TODO doc comments
        (string-append "; Element " ((element:get-class):get-simple-name) " not supported\n")))))
