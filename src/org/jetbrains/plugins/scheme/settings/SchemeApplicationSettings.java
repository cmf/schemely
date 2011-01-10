package org.jetbrains.plugins.scheme.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;


@State(
  name = "SchemeApplicationSettings",
  storages = {@Storage(
    id = "scheme_config",
    file = "$APP_CONFIG$/scheme_config.xml")})
public class SchemeApplicationSettings implements PersistentStateComponent<SchemeApplicationSettings>
{

  public SchemeApplicationSettings getState()
  {
    return this;
  }

  public void loadState(SchemeApplicationSettings schemeApplicationSettings)
  {
    XmlSerializerUtil.copyBean(schemeApplicationSettings, this);
  }

}
