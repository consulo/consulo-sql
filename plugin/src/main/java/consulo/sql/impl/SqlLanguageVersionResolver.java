package consulo.sql.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiUtilCore;
import consulo.language.version.LanguageVersion;
import consulo.language.version.LanguageVersionResolver;
import consulo.project.Project;
import consulo.sql.impl.configurable.SqlDialectService;
import consulo.sql.language.SqlDefaultVersionResolver;
import consulo.sql.language.SqlLanguage;
import consulo.sql.language.impl.version.sql92.Sql92LanguageVersion;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * @author VISTALL
 * @since 2026-05-03
 */
@ExtensionImpl
public class SqlLanguageVersionResolver implements LanguageVersionResolver {
    private LanguageVersion myDefaultVersion;

    public SqlLanguageVersionResolver() {
        myDefaultVersion = SqlLanguage.INSTANCE.findVersionByClass(Sql92LanguageVersion.class);
    }

    @RequiredReadAction
    @Override
    public LanguageVersion getLanguageVersion(Language language, PsiElement element) {
        if (element == null) {
            return myDefaultVersion;
        }
        return getLanguageVersion(language, element.getProject(), PsiUtilCore.getVirtualFile(element.getOriginalElement()));
    }

    @RequiredReadAction
    @Override
    public LanguageVersion getLanguageVersion(Language language, Project project, VirtualFile virtualFile) {
        if (project == null) {
            return myDefaultVersion;
        }

        if (virtualFile == null) {
            return getDefaultVersion(project, virtualFile);
        }
        SqlDialectService service = project.getInstance(SqlDialectService.class);
        return Objects.requireNonNull(service.getMapping(virtualFile));
    }

    private LanguageVersion getDefaultVersion(Project project, @Nullable VirtualFile file) {
        return project.getExtensionPoint(SqlDefaultVersionResolver.class)
            .computeSafeIfAny(resolver -> resolver.tryResolveDefaultVersion(file), myDefaultVersion);
    }

    @Override
    public Language getLanguage() {
        return SqlLanguage.INSTANCE;
    }
}
