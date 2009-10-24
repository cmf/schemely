package org.jetbrains.plugins.scheme.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.SchemeIcons;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SchemeColorsAndFontsPage implements ColorSettingsPage
{
  @NotNull
  public String getDisplayName()
  {
    return "Scheme";
  }

  @Nullable
  public Icon getIcon()
  {
    return SchemeIcons.SCHEME_ICON_16x16;
  }

  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors()
  {
    return ATTRS;
  }

  private static final
  AttributesDescriptor[]
    ATTRS =
    new AttributesDescriptor[]{new AttributesDescriptor(SchemeSyntaxHighlighter.LINE_COMMENT_ID,
                                                        SchemeSyntaxHighlighter.LINE_COMMENT),
                               new AttributesDescriptor(SchemeSyntaxHighlighter.ATOM_ID, SchemeSyntaxHighlighter.ATOM),
                               new AttributesDescriptor(SchemeSyntaxHighlighter.KEY_ID, SchemeSyntaxHighlighter.KEY),
                               new AttributesDescriptor(SchemeSyntaxHighlighter.NUMBER_ID,
                                                        SchemeSyntaxHighlighter.NUMBER),
                               new AttributesDescriptor(SchemeSyntaxHighlighter.STRING_ID,
                                                        SchemeSyntaxHighlighter.STRING),
                               new AttributesDescriptor(SchemeSyntaxHighlighter.BRACES_ID,
                                                        SchemeSyntaxHighlighter.BRACES),
                               new AttributesDescriptor(SchemeSyntaxHighlighter.PAREN_ID,
                                                        SchemeSyntaxHighlighter.PARENTS),
                               new AttributesDescriptor(SchemeSyntaxHighlighter.BAD_CHARACTER_ID,
                                                        SchemeSyntaxHighlighter.BAD_CHARACTER),
                               new AttributesDescriptor(SchemeSyntaxHighlighter.CHAR_ID, SchemeSyntaxHighlighter.CHAR),
                               new AttributesDescriptor(SchemeSyntaxHighlighter.LITERAL_ID,
                                                        SchemeSyntaxHighlighter.LITERAL),
                               new AttributesDescriptor(SchemeSyntaxHighlighter.DEF_ID, SchemeSyntaxHighlighter.DEF),};

  @NotNull
  public ColorDescriptor[] getColorDescriptors()
  {
    return new ColorDescriptor[0];
  }

  @NotNull
  public SyntaxHighlighter getHighlighter()
  {
    return new SchemeSyntaxHighlighter();
  }

  @NonNls
  @NotNull
  public String getDemoText()
  {
    return "; Example from Scheme Special Forms http://scheme.org/special_forms\n" +
           "; \n" +
           "\n" +
           "(<def>defn</def>\n" +
           "#^{:doc \"mymax [xs+] gets the maximum value in xs using > \"\n" +
           "   :test (fn []\n" +
           "             (assert (= 42  (max 2 42 5 4))))\n" +
           "   :user/comment \"this is the best fn ever!\"}\n" +
           "  mymax\n" +
           "  ([x] x)\n" +
           "  ([x y] (if (> x y) x y))\n" +
           "  ([x y & nil]\n" +
           "   (<def>reduce</def> mymax (mymax x y) more {\\tab \"  \"})))";
  }

  @Nullable
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()
  {
    Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
    map.put("def", SchemeSyntaxHighlighter.DEF);
    return map;
  }
}
