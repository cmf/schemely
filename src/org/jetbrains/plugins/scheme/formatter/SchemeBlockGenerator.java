package org.jetbrains.plugins.scheme.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.scheme.formatter.codeStyle.SchemeCodeStyleSettings;
import org.jetbrains.plugins.scheme.formatter.processors.SchemeIndentProcessor;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.plugins.scheme.psi.api.SchemeList;
import org.jetbrains.plugins.scheme.psi.api.SchemeLiteral;
import org.jetbrains.plugins.scheme.psi.api.SchemeVector;
import org.jetbrains.plugins.scheme.psi.api.symbols.SchemeIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ilyas
 */
public class SchemeBlockGenerator
{
  private static final TokenSet RIGHT_BRACES = TokenSet.create(Tokens.RIGHT_CURLY, Tokens.RIGHT_SQUARE);

  public static List<Block> generateSubBlocks(ASTNode node, Wrap wrap, CodeStyleSettings settings, SchemeBlock block)
  {
    PsiElement blockPsi = block.getNode().getPsi();

    ArrayList<Block> subBlocks = new ArrayList<Block>();
    ASTNode children[] = node.getChildren(null);
    ASTNode prevChildNode = null;

    Alignment childAlignment = Alignment.createAlignment();
    for (ASTNode childNode : children)
    {
      if (canBeCorrectBlock(childNode))
      {
        SchemeCodeStyleSettings styleSettings = block.getSettings().getCustomSettings(SchemeCodeStyleSettings.class);
        Alignment align = mustAlign(blockPsi, childNode.getPsi(), styleSettings) ? childAlignment : null;

        Indent indent = SchemeIndentProcessor.getChildIndent(block, prevChildNode, childNode);
        subBlocks.add(new SchemeBlock(childNode, align, indent, wrap, settings));
        prevChildNode = childNode;
      }
    }
    return subBlocks;
  }

  public static boolean mustAlign(PsiElement blockPsi, PsiElement child, SchemeCodeStyleSettings settings)
  {
    if (blockPsi instanceof SchemeVector)
    {
      return !(child instanceof LeafPsiElement) ||
             RIGHT_BRACES.contains(child.getNode().getElementType()) ||
             (child instanceof PsiComment);
    }

    if (settings.ALIGN_SCHEME_FORMS)
    {
      if (blockPsi instanceof SchemeList /* && !(blockPsi instanceof ClDef) */ )
      {
        SchemeList list = (SchemeList) blockPsi;
        PsiElement first = list.getFirstNonLeafElement();

        int start;
        if (first == null || !(first instanceof SchemeIdentifier))
        {
          start = 0;
        }
        else if (isDefineLike(first))
        {
          start = 2;
        }
        else
        {
          start = 1;
        }

        if ((start == 0) &&
            ((first == null) || (first.getTextRange().getStartOffset() <= child.getTextRange().getStartOffset())))
        {
          return true;
        }
        else if ((start == 1) && (first.getTextRange().getEndOffset() <= child.getTextRange().getStartOffset()))
        {
          return true;
        }
        else if ((start == 2))
        {
          PsiElement second = list.getSecondNonLeafElement();
          if ((second != null) && (second.getTextRange().getEndOffset() <= child.getTextRange().getStartOffset()))
          {
            return true;
          }
        }
      }
    }

    if (blockPsi instanceof SchemeLiteral)
    {
      ASTNode node = blockPsi.getNode();
      assert node != null;
      ASTNode[] elements = node.getChildren(null);
      if (elements.length > 0 && elements[0].getElementType() == Tokens.STRING_LITERAL)
      {
        return true;
      }
    }
    return false;
  }

  private static boolean isDefineLike(PsiElement first)
  {
    return first.getText().equalsIgnoreCase("lambda") ||
           first.getText().equalsIgnoreCase("define") ||
           first.getText().equalsIgnoreCase("let") ||
           first.getText().equalsIgnoreCase("letrec") ||
           first.getText().equalsIgnoreCase("let*") ||
           first.getText().equalsIgnoreCase("define-syntax") ||
           first.getText().equalsIgnoreCase("let-syntax") ||
           first.getText().equalsIgnoreCase("letrec-syntax");
  }

  private static boolean canBeCorrectBlock(ASTNode node)
  {
    String nodeText = node.getText().trim();
    return (nodeText.length() > 0);
  }
}
