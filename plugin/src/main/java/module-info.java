/**
 * @author VISTALL
 * @since 2026-05-02
 */
module consulo.sql {
    requires consulo.application.api;

    requires consulo.ui.ex.api;

    // TODO remove in future
    requires consulo.ide.impl;
    requires consulo.language.api;
    requires consulo.language.impl;
    requires consulo.sql.language.api;
    requires consulo.sql.language.impl;
}