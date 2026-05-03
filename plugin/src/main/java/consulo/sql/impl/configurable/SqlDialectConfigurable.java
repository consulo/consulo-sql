package consulo.sql.impl.configurable;

import consulo.annotation.component.ExtensionImpl;
import consulo.configurable.ProjectConfigurable;
import consulo.configurable.StandardConfigurableIds;
import consulo.ide.impl.idea.util.ui.tree.PerFileConfigurableBase;
import consulo.language.impl.util.LanguagePerFileMappings;
import consulo.language.localize.LanguageLocalize;
import consulo.language.version.LanguageVersion;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.ex.ColoredTextContainer;
import consulo.util.dataholder.Key;
import consulo.util.lang.StringUtil;
import consulo.util.lang.Trinity;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author VISTALL
 * @since 2026-05-03
 */
@ExtensionImpl
public class SqlDialectConfigurable extends PerFileConfigurableBase<LanguageVersion> implements ProjectConfigurable {
    private final Trinity<String, Supplier<LanguageVersion>, Consumer<LanguageVersion>> myProjectMapping;

    @Inject
    public SqlDialectConfigurable(Project project, SqlDialectService mappings) {
        super(project, mappings);
        myProjectMapping =
            Trinity.create(LanguageLocalize.perfileconfigurablebaseProjectMapping(LanguageLocalize.perfileconfigurablebaseMappingTitle()).get(), () -> mappings.getConfiguredMapping(null), o -> myMappings.setMapping(null, o));
    }

    @Override
    protected boolean isProjectMapping(Trinity<String, Supplier<LanguageVersion>, Consumer<LanguageVersion>> prop) {
        return prop == myProjectMapping;
    }

    @Override
    protected List<Trinity<String, Supplier<LanguageVersion>, Consumer<LanguageVersion>>> getDefaultMappings() {
        return List.of(myProjectMapping);
    }

    @Override
    protected @Nullable <S> Object getParameter(Key<S> key) {
        return null;
    }

    @Override
    protected void renderValue(Object target, LanguageVersion languageVersion, ColoredTextContainer coloredTextContainer) {
        coloredTextContainer.append(languageVersion.getName());
    }

    @Override
    public String getId() {
        return "sql.editor";
    }

    @Override
    public @Nullable String getParentId() {
        return StandardConfigurableIds.EDITOR_GROUP;
    }

    @Override
    public LocalizeValue getDisplayName() {
        return LocalizeValue.localizeTODO("SQL & SQL Dialects");
    }
}
