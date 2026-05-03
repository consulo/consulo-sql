package consulo.sql.language;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.language.version.LanguageVersion;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 2026-05-03
 */
@ExtensionAPI(ComponentScope.PROJECT)
public interface SqlDefaultVersionResolver {
    @Nullable
    LanguageVersion tryResolveDefaultVersion(@Nullable VirtualFile virtualFile);
}
