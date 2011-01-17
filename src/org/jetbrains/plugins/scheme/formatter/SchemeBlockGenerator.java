package org.jetbrains.plugins.scheme.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.plugins.scheme.lexer.Tokens;
import org.jetbrains.plugins.scheme.psi.impl.SchemeVector;
import org.jetbrains.plugins.scheme.psi.impl.list.SchemeList;
import org.jetbrains.plugins.scheme.psi.impl.symbols.SchemeIdentifier;

import java.util.*;


public class SchemeBlockGenerator
{
  private static final Map<String, Integer> schemeFormIndent = new HashMap<String, Integer>();

  static
  {
    // From scheme.el && iuscheme.el
    schemeFormIndent.put("begin", 0);
    schemeFormIndent.put("define", 1);
    schemeFormIndent.put("define-syntax", 1);
    schemeFormIndent.put("define-class", 2);
    schemeFormIndent.put("define-simple-class", 2);
    schemeFormIndent.put("case", 1);
    schemeFormIndent.put("delay", 0);
    schemeFormIndent.put("do", 2);
    schemeFormIndent.put("lambda", 1);
    schemeFormIndent.put("let", 1);
    schemeFormIndent.put("let*", 1);
    schemeFormIndent.put("letrec", 1);
    schemeFormIndent.put("let-values", 1);
    schemeFormIndent.put("let*-values", 1);
    schemeFormIndent.put("sequence", 0);
    schemeFormIndent.put("let-syntax", 1);
    schemeFormIndent.put("letrec-syntax", 1);
    schemeFormIndent.put("syntax-rules", 1);
    schemeFormIndent.put("syntax-case", 2);
    schemeFormIndent.put("and", 0);
    schemeFormIndent.put("or", 0);
    schemeFormIndent.put("cond", 0);
    schemeFormIndent.put("set!", 1);
    schemeFormIndent.put("if", 3);
    schemeFormIndent.put("when", 1);
    schemeFormIndent.put("unless", 1);
    schemeFormIndent.put("parameterize", 1);
    schemeFormIndent.put("with-syntax", 1);

    schemeFormIndent.put("call-with-input-file", 1);
    schemeFormIndent.put("with-input-from-file", 1);
    schemeFormIndent.put("with-input-from-port", 1);
    schemeFormIndent.put("call-with-output-file", 1);
    schemeFormIndent.put("with-output-to-file", 1);
    schemeFormIndent.put("with-output-to-port", 1);
    schemeFormIndent.put("call-with-values", 1);
    schemeFormIndent.put("dynamic-wind", 3);
  }

  public static List<Block> generateSubBlocks(ASTNode node, Wrap wrap, CodeStyleSettings settings, SchemeBlock block)
  {
    PsiElement blockPsi = node.getPsi();

    Collection<ASTNode> children = getChildren(node);

    if (blockPsi instanceof SchemeList)
    {
      SchemeList list = (SchemeList) blockPsi;

      PsiElement first = list.getFirstNonLeafElement();
      if (first instanceof SchemeIdentifier)
      {
        int parameters = 0;
        Alignment parameterAlignment = Alignment.createAlignment();
        Alignment bodyAlignment = Alignment.createAlignment();

        List<Block> subBlocks = new ArrayList<Block>();

        String operator = first.getText();
        Integer integer = schemeFormIndent.get(operator);
        if (integer != null)
        {
          parameters = integer.intValue();
        }
        if (operator.equals("let"))
        {
          // Special case named let
          PsiElement element = list.getSecondNonLeafElement();
          if (element instanceof SchemeIdentifier)
          {
            parameters = 2;
          }
        }

        int childIndex = 0;
        for (ASTNode childNode : children)
        {
          Alignment align = null;
          Indent indent;

          if (Tokens.BRACES.contains(childNode.getElementType()))
          {
            indent = Indent.getNoneIndent();
          }
          else
          {
            if (childIndex == 0)
            {
              indent = Indent.getNormalIndent(true);
            }
            else if ((childIndex - 1) < parameters)
            {
              align = parameterAlignment;
              indent = Indent.getContinuationIndent(true);
            }
            else
            {
              align = bodyAlignment;
              indent = Indent.getNormalIndent(true);
            }
            childIndex++;
          }
          subBlocks.add(new SchemeBlock(childNode, align, indent, wrap, settings));
        }
        return subBlocks;
      }
      else
      {
        return indentNormalList(children, Alignment.createAlignment(), wrap, settings);
      }
    }
    else if (blockPsi instanceof SchemeVector)
    {
      return indentNormalList(children, null, wrap, settings);
    }
    else
    {
      return createSubBlocksWith(children, Indent.getNoneIndent(), wrap, settings);
    }
  }

  private static List<Block> indentNormalList(Collection<ASTNode> children,
                                              Alignment alignment,
                                              Wrap wrap,
                                              CodeStyleSettings settings)
  {
    List<Block> subBlocks = new ArrayList<Block>();
    for (ASTNode childNode : children)
    {
      Indent indent;
      Alignment align;
      if (Tokens.BRACES.contains(childNode.getElementType()))
      {
        indent = Indent.getNoneIndent();
        align = null;
      }
      else
      {
        indent = Indent.getNormalIndent(true);
        align = alignment;
      }
      subBlocks.add(new SchemeBlock(childNode, align, indent, wrap, settings));
    }
    return subBlocks;
  }

  private static ArrayList<Block> createSubBlocksWith(Collection<ASTNode> children,
                                                      Indent indent,
                                                      Wrap wrap,
                                                      CodeStyleSettings settings)
  {
    ArrayList<Block> subBlocks = new ArrayList<Block>();
    for (ASTNode childNode : children)
    {
      subBlocks.add(new SchemeBlock(childNode, null, indent, wrap, settings));
    }
    return subBlocks;
  }

  private static Collection<ASTNode> getChildren(ASTNode node)
  {
    Collection<ASTNode> ret = new ArrayList<ASTNode>();
    for (ASTNode astNode : node.getChildren(null))
    {
      if (nonEmptyBlock(astNode))
      {
        ret.add(astNode);
      }
    }
    return ret;
  }

  private static boolean nonEmptyBlock(ASTNode node)
  {
    String nodeText = node.getText().trim();
    return (nodeText.length() > 0);
  }
}
