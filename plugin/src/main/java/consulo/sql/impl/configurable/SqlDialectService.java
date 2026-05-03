package consulo.sql.impl.configurable;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.language.impl.util.LanguagePerFileMappings;
import consulo.language.impl.util.PerFileMappingsBase;
import consulo.language.localize.LanguageLocalize;
import consulo.language.version.LanguageVersion;
import consulo.project.Project;
import consulo.sql.language.SqlDefaultVersionResolver;
import consulo.sql.language.SqlLanguage;
import consulo.sql.language.impl.version.sql92.Sql92LanguageVersion;
import consulo.util.lang.StringUtil;
import consulo.util.lang.Trinity;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author VISTALL
 * @since 2026-05-03
 */
@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
@State(name = "SqlDialectService", storages = @Storage("sql.xml"))
public class SqlDialectService extends PerFileMappingsBase<LanguageVersion> {
    private final Project myProject;
    private LanguageVersion myDefaultVersion;

    @Inject
    public SqlDialectService(Project project) {
        myProject = project;
        myDefaultVersion = SqlLanguage.INSTANCE.findVersionByClass(Sql92LanguageVersion.class);
    }


    @Nullable
    @Override
    public LanguageVersion getDefaultMapping(VirtualFile file) {
        return myProject.getExtensionPoint(SqlDefaultVersionResolver.class)
            .computeSafeIfAny(resolver -> resolver.tryResolveDefaultVersion(file), myDefaultVersion);
    }

    @Override
    public List<LanguageVersion> getAvailableValues() {
        return List.of(SqlLanguage.INSTANCE.getVersions());
    }

    @Override
    protected @Nullable String serialize(LanguageVersion languageVersion) {
        return languageVersion.getId();
    }
}
