package consulo.sql.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.version.LanguageVersion;
import consulo.sql.language.SqlDefaultVersionResolver;
import consulo.sql.language.SqlLanguage;
import consulo.sql.language.impl.version.sql92.Sql92LanguageVersion;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 2026-05-03
 */
@ExtensionImpl(order = "last")
public class SqlDefaultVersionResolverImpl implements SqlDefaultVersionResolver {
    @Override
    public @Nullable LanguageVersion tryResolveDefaultVersion(VirtualFile virtualFile) {
        return SqlLanguage.INSTANCE.findVersionByClass(Sql92LanguageVersion.class);
    }
}
