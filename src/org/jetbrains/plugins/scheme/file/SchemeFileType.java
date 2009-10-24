package org.jetbrains.plugins.scheme.file;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scheme.SchemeIcons;
import org.jetbrains.plugins.scheme.SchemeLanguage;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: merlyn
 * Date: 16-Nov-2008
 * Time: 11:08:03 PM
 * Copyright 2007, 2008 Red Shark Technology
 * <p/>
 * Copyright 2000-2007 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SchemeFileType extends LanguageFileType
{
  public static final SchemeFileType SCHEME_FILE_TYPE = new SchemeFileType();
  public static final Language SCHEME_LANGUAGE = SCHEME_FILE_TYPE.getLanguage();
  @NonNls
  public static final String SCHEME_DEFAULT_EXTENSION = "scm";


  public SchemeFileType()
  {
    super(new SchemeLanguage());
  }

  @NotNull
  public String getName()
  {
    return "Scheme";
  }

  @NotNull
  public String getDescription()
  {
    return "Scheme file";
  }

  @NotNull
  public String getDefaultExtension()
  {
    return "scm";
  }

  public Icon getIcon()
  {
    return SchemeIcons.SCHEME_ICON_16x16;
  }

  public boolean isJVMDebuggingSupported()
  {
    return true;
  }


}
