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

package consulo.sql.language.impl.test;

import consulo.language.file.LanguageFileType;
import consulo.sql.language.SqlFileType;
import consulo.test.junit.impl.language.SimpleParsingTest;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author VISTALL
 * @since 2026-02-28
 */
public class SqlParsingTest extends SimpleParsingTest<Object> {
    public SqlParsingTest() {
        super("sqlParsing", "sql");
    }

    @Test
    public void testSelectSimple(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testSelectJoin(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testSelectWhere(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testSelectGroupBy(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testSelectExpressions(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testInsert(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testUpdate(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testDelete(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testCreateTable(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testCreateViewIndex(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testDrop(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testAlterTable(Context context) throws Exception {
        doTest(context, null);
    }

    @Nonnull
    @Override
    protected LanguageFileType getFileType(@Nonnull Context context, @Nullable Object testContext) {
        return SqlFileType.INSTANCE;
    }
}
