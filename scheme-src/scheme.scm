;(define-namespace Language "class:com.intellij.lang.Language")
;(define-namespace String "class:java.lang.String")
;(define-namespace LanguageFileType "class:import com.intellij.openapi.fileTypes.LanguageFileType")
;
;(define-simple-class SchemeLanguage (Language)
;                     (id :: String allocation: 'static init-value: "Scheme")
;                     ((*init*) (invoke-special Language (this) '*init* id (String[]))))
;
;(define-simple-class FileType (LanguageFileType)
;                     (type :: FileType allocation: 'static init-value: (FileType)))
