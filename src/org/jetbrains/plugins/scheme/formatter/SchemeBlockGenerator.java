package org.jetbrains.plugins.scheme.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.plugins.scheme.formatter.processors.SchemeIndentProcessor;
import org.jetbrains.plugins.scheme.formatter.codeStyle.SchemeCodeStyleSettings;
import org.jetbrains.plugins.scheme.psi.api.*;
import org.jetbrains.plugins.scheme.psi.api.defs.ClDef;
import org.jetbrains.plugins.scheme.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.scheme.lexer.Tokens;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ilyas
 */
public class SchemeBlockGenerator
{
  private static ASTNode myNode;
  private static Alignment myAlignment;
  private static Wrap myWrap;
  private static CodeStyleSettings mySettings;
  private static SchemeBlock myBlock;
  private static final TokenSet RIGHT_BRACES = TokenSet.create(Tokens.RIGHT_CURLY, Tokens.RIGHT_SQUARE);

  public static List<Block> generateSubBlocks(ASTNode node,
                                              Alignment alignment,
                                              Wrap wrap,
                                              CodeStyleSettings settings,
                                              SchemeBlock block)
  {
    myNode = node;
    myWrap = wrap;
    mySettings = settings;
    myAlignment = alignment;
    myBlock = block;

    PsiElement blockPsi = myBlock.getNode().getPsi();

    final ArrayList<Block> subBlocks = new ArrayList<Block>();
    ASTNode children[] = myNode.getChildren(null);
    ASTNode prevChildNode = null;


    final Alignment childAlignment = Alignment.createAlignment();
    for (ASTNode childNode : children)
    {
      if (canBeCorrectBlock(childNode))
      {
        SchemeCodeStyleSettings styleSettings = block.getSettings().getCustomSettings(SchemeCodeStyleSettings.class);
        final Alignment align = mustAlign(blockPsi, childNode.getPsi(), styleSettings) ? childAlignment : null;

        //        if (align != null)
        //        {
        //          myBlock.setAlignment(align);
        //        }
        final Indent indent = SchemeIndentProcessor.getChildIndent(myBlock, prevChildNode, childNode);
        subBlocks.add(new SchemeBlock(childNode, align, indent, myWrap, mySettings));
        prevChildNode = childNode;
      }
    }
    return subBlocks;
  }

  public static boolean mustAlign(PsiElement blockPsi, PsiElement child, SchemeCodeStyleSettings settings)
  {
    if (blockPsi instanceof ClVector)
    {
      return !(child instanceof LeafPsiElement) ||
             RIGHT_BRACES.contains(child.getNode().getElementType()) ||
             (child instanceof PsiComment);
    }

    if (settings.ALIGN_SCHEME_FORMS)
    {
      if (blockPsi instanceof ClList && !(blockPsi instanceof ClDef))
      {
        final ClList list = (ClList) blockPsi;
        PsiElement first = list.getFirstNonLeafElement();
        if (first == child && !applicationStart(first))
        {
          return true;
        }
        if (first != null &&
            !applicationStart(first) &&
            first.getTextRange().getEndOffset() <= child.getTextRange().getStartOffset())
        {
          return true;
        }
        final PsiElement second = list.getSecondNonLeafElement();
        if (second != null && second.getTextRange().getEndOffset() <= child.getTextRange().getStartOffset())
        {
          return true;
        }
      }
    }

    if (blockPsi instanceof ClLiteral)
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

  private static boolean applicationStart(PsiElement first)
  {
    return first instanceof ClSymbol;
  }

  private static boolean canBeCorrectBlock(final ASTNode node)
  {
    return (node.getText().trim().length() > 0);
  }
}
