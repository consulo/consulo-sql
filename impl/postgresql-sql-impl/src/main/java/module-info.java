/**
 * @author VISTALL
 * @since 2026-03-17
 */
module consulo.sql.impl.postgresql {
    requires consulo.sql.language.api;
    requires consulo.sql.language.impl;
    requires consulo.language.impl;

    exports consulo.sql.language.impl.postgresql;
}
