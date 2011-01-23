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
  final protected ASTNode myNode;
  final protected Alignment myAlignment;
  final protected Indent myIndent;
  final protected Wrap myWrap;
  final protected CodeStyleSettings mySettings;
  protected Alignment myChildAlignment = Alignment.createAlignment();

  protected List<Block> mySubBlocks = null;


  public SchemeBlock(@NotNull ASTNode node,
                     @Nullable Alignment alignment,
                     @NotNull Indent indent,
                     @Nullable Wrap wrap,
                     CodeStyleSettings settings)
  {
    myNode = node;
    myAlignment = alignment;
    setAlignment(alignment);
    myIndent = indent;
    myWrap = wrap;
    mySettings = settings;
  }

  @NotNull
  public ASTNode getNode()
  {
    return myNode;
  }

  @NotNull
  public CodeStyleSettings getSettings()
  {
    return mySettings;
  }

  @NotNull
  public TextRange getTextRange()
  {
    return myNode.getTextRange();
  }

  @NotNull
  public List<Block> getSubBlocks()
  {
    if (mySubBlocks == null)
    {
      mySubBlocks = SchemeBlockGenerator.generateSubBlocks(myNode, myWrap, mySettings, this);
    }
    return mySubBlocks;
  }

  @Nullable
  public Wrap getWrap()
  {
    return myWrap;
  }

  @Nullable
  public Indent getIndent()
  {
    return myIndent;
  }

  @Nullable
  public Alignment getAlignment()
  {
    return myAlignment;
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
      return new ChildAttributes(Indent.getNormalIndent(true), myChildAlignment);
    }
    return new ChildAttributes(Indent.getNoneIndent(), null);
  }


  public boolean isIncomplete()
  {
    return isIncomplete(myNode);
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
    return myNode.getFirstChildNode() == null;
  }

  public void setAlignment(Alignment alignment)
  {
    myChildAlignment = alignment;
  }
}
