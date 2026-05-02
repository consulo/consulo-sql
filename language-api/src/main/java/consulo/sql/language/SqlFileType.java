/*
 * Copyright 2013-2020 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.sql.language;

import consulo.language.file.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.sql.lang.api.icon.SqlLangApiIconGroup;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2020-10-31
 */
public class SqlFileType extends LanguageFileType {
    public static final SqlFileType INSTANCE = new SqlFileType();

    private SqlFileType() {
        super(SqlLanguage.INSTANCE);
    }

    @Nonnull
    @Override
    public String getId() {
        return "SQL";
    }

    @Nonnull
    @Override
    public LocalizeValue getDescription() {
        return LocalizeValue.localizeTODO("SQL files");
    }

    @Nonnull
    @Override
    public String getDefaultExtension() {
        return "sql";
    }

    @Nonnull
    @Override
    public Image getIcon() {
        return SqlLangApiIconGroup.sql();
    }
}
