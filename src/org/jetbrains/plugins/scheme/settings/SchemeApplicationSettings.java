package org.jetbrains.plugins.scheme.settings;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * @author ilyas
 */
@State(
  name = "SchemeApplicationSettings",
  storages = {@Storage(
    id = "scala_config",
    file = "$APP_CONFIG$/scheme_config.xml")})
public class SchemeApplicationSettings implements PersistentStateComponent<SchemeApplicationSettings>
{
  public String[] CONSOLE_HISTORY = new String[0];

  public SchemeApplicationSettings getState()
  {
    return this;
  }

  public void loadState(SchemeApplicationSettings schemeApplicationSettings)
  {
    XmlSerializerUtil.copyBean(schemeApplicationSettings, this);
  }

  public static SchemeApplicationSettings getInstance()
  {
    return ServiceManager.getService(SchemeApplicationSettings.class);
  }

}
