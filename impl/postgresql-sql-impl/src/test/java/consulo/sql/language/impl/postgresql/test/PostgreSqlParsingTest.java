/*
 * Copyright 2013-2026 consulo.io
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

package consulo.sql.language.impl.postgresql.test;

import consulo.language.file.LanguageFileType;
import consulo.language.version.LanguageVersion;
import consulo.sql.language.SqlFileType;
import consulo.sql.language.SqlLanguage;
import consulo.sql.language.impl.postgresql.PostgreSqlLanguageVersion;
import consulo.test.junit.impl.language.SimpleParsingTest;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author VISTALL
 * @since 2026-03-17
 */
public class PostgreSqlParsingTest extends SimpleParsingTest<Object> {
    public PostgreSqlParsingTest() {
        super("psqlParsing", "sql");
    }

    @Test
    public void testCreateFunction(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testCreateProcedure(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testCreateOrReplaceFunction(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testDropFunction(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testKeywordAsIdentifier(Context context) throws Exception {
        doTest(context, null);
    }

    @Nonnull
    @Override
    protected LanguageFileType getFileType(@Nonnull Context context, @Nullable Object testContext) {
        return SqlFileType.INSTANCE;
    }

    @Override
    protected LanguageVersion resolveLanguageVersion(Context context, @Nullable Object testContext, FileType fileType) {
        for (LanguageVersion version : SqlLanguage.INSTANCE.getVersions()) {
            if (version instanceof PostgreSqlLanguageVersion) {
                return version;
            }
        }
        throw new IllegalStateException("PostgreSqlLanguageVersion not found");
    }
}
