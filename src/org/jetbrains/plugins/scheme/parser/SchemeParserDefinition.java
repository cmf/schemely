package org.jetbrains.plugins.scheme.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.lexer.SchemeLexer;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.plugins.scheme.psi.impl.SchemeFile;


public class SchemeParserDefinition implements ParserDefinition
{
  @NotNull
  public Lexer createLexer(Project project)
  {
    return new SchemeLexer();
  }

  public PsiParser createParser(Project project)
  {
    return new SchemeParser();
  }

  public IFileElementType getFileNodeType()
  {
    return AST.FILE;
  }

  @NotNull
  public TokenSet getWhitespaceTokens()
  {
    return Tokens.WHITESPACE_SET;
  }

  @NotNull
  public TokenSet getCommentTokens()
  {
    return Tokens.COMMENTS;
  }

  @NotNull
  public TokenSet getStringLiteralElements()
  {
    return Tokens.STRINGS;
  }

  @NotNull
  public PsiElement createElement(ASTNode node)
  {
    return SchemePsiCreator.createElement(node);
  }

  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right)
  {
    if (Tokens.PREFIXES.contains(left.getElementType()))
    {
      return SpaceRequirements.MUST_NOT;
    }
    else if (left.getElementType() == Tokens.LEFT_PAREN ||
             right.getElementType() == Tokens.RIGHT_PAREN ||
             left.getElementType() == Tokens.RIGHT_PAREN ||
             right.getElementType() == Tokens.LEFT_PAREN

             ||
             left.getElementType() == Tokens.LEFT_CURLY ||
             right.getElementType() == Tokens.RIGHT_CURLY ||
             left.getElementType() == Tokens.RIGHT_CURLY ||
             right.getElementType() == Tokens.LEFT_CURLY

             ||
             left.getElementType() == Tokens.LEFT_SQUARE ||
             right.getElementType() == Tokens.RIGHT_SQUARE ||
             left.getElementType() == Tokens.RIGHT_SQUARE ||
             right.getElementType() == Tokens.LEFT_SQUARE)
    {
      return SpaceRequirements.MAY;
    }
    return SpaceRequirements.MUST;
  }

  public PsiFile createFile(FileViewProvider viewProvider)
  {
    return new SchemeFile(viewProvider);
  }
}

