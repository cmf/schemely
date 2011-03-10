Schemely
========

A Scheme support mode for IntelliJ 10
-------------------------------------

Schemely is an editing mode for Scheme code in IntelliJ 10. *This code is currently
unstable and only suitable for enthusiastic alpha/beta testers*.

### Features:

- Basics (paren matching, indenting, syntax highlighting)
- Symbol navigation/Rename/Find Usages
- Supports SISC and Kawa
- Interactive REPL with history, completion and syntax highlighting
- Compilation
- Structure view

Currently the code is undergoing some large changes, and lots of things don't work:

- Kawa support is almost totally broken
- Only an in-process SISC REPL can be used
- Symbol resolution doesn't work across files
- Symbol resolution (which affects navigation, rename and find usages) is currently broken
  in the face of macros
- Structure view is almost totally broken

Fixing at least the first 3 of these is my top priority and hopefully won't take too long.

There is also a long list of planned features:

- Finish running/compilation support
- Proper support for modules
- Customisable indentation
- Cross-file symbol resolution (support import, load and friends)
- Symbol resolution in the presence of macros (this is kind of hard)
- Macro support (expand macro)
- Extract function, extract variable, extract let binding
- Paredit type support
- Resolution of Java symbols for JVM-based Schemes
- Racket support
- Support for quirks of various implementations (case sensitivity etc)
- Debugging support
- Support for Android development with Kawa
- Documentation
