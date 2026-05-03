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

package consulo.sql.language.impl.jpql.test;

import consulo.language.file.LanguageFileType;
import consulo.language.version.LanguageVersion;
import consulo.sql.language.SqlFileType;
import consulo.sql.language.SqlLanguage;
import consulo.sql.language.impl.jpql.JpqlLanguageVersion;
import consulo.test.junit.impl.language.SimpleParsingTest;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author VISTALL
 * @since 2026-05-03
 */
public class JpqlParsingTest extends SimpleParsingTest<Object> {
    public JpqlParsingTest() {
        super("jpqlParsing", "sql");
    }

    @Test
    public void testAnonymousPlaceholder(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testPositionPlaceholder(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testNamedPlaceholder(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testIsEmpty(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testIsNotEmpty(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testMemberOf(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testNotMemberOf(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testTreat(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testPathFunctionKey(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testPathFunctionSize(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testPathFunctionType(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testNamedFunctionCall(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testJoinFetch(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testLowerConcatLike(Context context) throws Exception {
        doTest(context, null);
    }

    @Nonnull
    @Override
    protected LanguageFileType getFileType(@Nonnull Context context, @Nullable Object testContext) {
        return SqlFileType.INSTANCE;
    }

    @Override
    protected LanguageVersion resolveLanguageVersion(Context context, @Nullable Object testContext, FileType fileType) {
        return SqlLanguage.INSTANCE.findVersionByClass(JpqlLanguageVersion.class);
    }
}
