package schemely.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.formatter.processors.SchemeSpacingProcessor;
import schemely.parser.AST;
import schemely.psi.impl.SchemeFile;

import java.util.List;


public class SchemeBlock implements Block, AST
{
  final protected ASTNode node;
  final protected Alignment alignment;
  final protected Indent indent;
  final protected Wrap wrap;
  final protected CodeStyleSettings settings;
  protected Alignment childAlignment = Alignment.createAlignment();

  protected List<Block> subBlocks = null;


  public SchemeBlock(@NotNull ASTNode node,
                     @Nullable Alignment alignment,
                     @NotNull Indent indent,
                     @Nullable Wrap wrap,
                     CodeStyleSettings settings)
  {
    this.node = node;
    this.alignment = alignment;
    setAlignment(alignment);
    this.indent = indent;
    this.wrap = wrap;
    this.settings = settings;
  }

  @NotNull
  public ASTNode getNode()
  {
    return node;
  }

  @NotNull
  public CodeStyleSettings getSettings()
  {
    return settings;
  }

  @NotNull
  public TextRange getTextRange()
  {
    return node.getTextRange();
  }

  @NotNull
  public List<Block> getSubBlocks()
  {
    if (subBlocks == null)
    {
      subBlocks = SchemeBlockGenerator.generateSubBlocks(node, wrap, settings, this);
    }
    return subBlocks;
  }

  @Nullable
  public Wrap getWrap()
  {
    return wrap;
  }

  @Nullable
  public Indent getIndent()
  {
    return indent;
  }

  @Nullable
  public Alignment getAlignment()
  {
    return alignment;
  }

  public Spacing getSpacing(Block child1, Block child2)
  {
    return SchemeSpacingProcessor.getSpacing(child1, child2);
  }

  @NotNull
  public ChildAttributes getChildAttributes(int newChildIndex)
  {
    return getAttributesByParent();
  }

  private ChildAttributes getAttributesByParent()
  {
    ASTNode astNode = getNode();
    PsiElement psiParent = astNode.getPsi();
    if (psiParent instanceof SchemeFile)
    {
      return new ChildAttributes(Indent.getNoneIndent(), null);
    }
    if (LIST_LIKE_FORMS.contains(astNode.getElementType()))
    {
      return new ChildAttributes(Indent.getNormalIndent(true), childAlignment);
    }
    return new ChildAttributes(Indent.getNoneIndent(), null);
  }


  public boolean isIncomplete()
  {
    return isIncomplete(node);
  }

  /**
   * @param node Tree node
   * @return true if node is incomplete
   */
  public boolean isIncomplete(@NotNull ASTNode node)
  {
    ASTNode lastChild = node.getLastChildNode();
    while (lastChild != null &&
           (lastChild.getPsi() instanceof PsiWhiteSpace || lastChild.getPsi() instanceof PsiComment))
    {
      lastChild = lastChild.getTreePrev();
    }
    return lastChild != null &&
           (lastChild.getPsi() instanceof PsiErrorElement || isIncomplete(lastChild));
  }

  public boolean isLeaf()
  {
    return node.getFirstChildNode() == null;
  }

  public void setAlignment(Alignment alignment)
  {
    childAlignment = alignment;
  }
}
