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
    return SchemeIcons.SCHEME_ICON;
  }

  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors()
  {
    return ATTRS;
  }

  private static final
  AttributesDescriptor[]
    ATTRS =
    new AttributesDescriptor[]{desc(SchemeSyntaxHighlighter.IDENTIFIER_ID, SchemeSyntaxHighlighter.IDENTIFIER),
                               desc(SchemeSyntaxHighlighter.COMMENT_ID, SchemeSyntaxHighlighter.LINE_COMMENT),
                               desc(SchemeSyntaxHighlighter.NUMBER_ID, SchemeSyntaxHighlighter.NUMBER),
                               desc(SchemeSyntaxHighlighter.STRING_ID, SchemeSyntaxHighlighter.STRING),
                               desc(SchemeSyntaxHighlighter.BRACES_ID, SchemeSyntaxHighlighter.BRACE),
                               desc(SchemeSyntaxHighlighter.PAREN_ID, SchemeSyntaxHighlighter.PAREN),
                               desc(SchemeSyntaxHighlighter.BAD_CHARACTER_ID, SchemeSyntaxHighlighter.BAD_CHARACTER),
                               desc(SchemeSyntaxHighlighter.CHAR_ID, SchemeSyntaxHighlighter.CHAR),
                               desc(SchemeSyntaxHighlighter.LITERAL_ID, SchemeSyntaxHighlighter.LITERAL),
                               desc(SchemeSyntaxHighlighter.QUOTED_ID, SchemeSyntaxHighlighter.QUOTED),
                               desc(SchemeSyntaxHighlighter.KEYWORD_ID, SchemeSyntaxHighlighter.KEYWORD),
                               desc(SchemeSyntaxHighlighter.SPECIAL_ID, SchemeSyntaxHighlighter.SPECIAL),};

  private static AttributesDescriptor desc(String displayName, TextAttributesKey key)
  {
    return new AttributesDescriptor(displayName, key);
  }

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
    map.put("def", SchemeSyntaxHighlighter.IDENTIFIER);
    return map;
  }
}
