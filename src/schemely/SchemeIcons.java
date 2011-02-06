package schemely;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;

public interface SchemeIcons
{
  @NonNls
  final Icon SCHEME_ICON = IconLoader.findIcon("/schemely/icons/scheme_icon_16x16.png");

  final Icon FUNCTION = IconLoader.findIcon("/schemely/icons/def_tmp.png");
  final Icon METHOD = IconLoader.findIcon("/schemely/icons/meth_tmp.png");
  final Icon JAVA_METHOD = IconLoader.findIcon("/schemely/icons/method.png");
  final Icon JAVA_FIELD = IconLoader.findIcon("/schemely/icons/field.png");
  final Icon SYMBOL = IconLoader.findIcon("/schemely/icons/symbol.png");
  final Icon NAMESPACE = IconLoader.findIcon("/schemely/icons/namespace.png");
}
