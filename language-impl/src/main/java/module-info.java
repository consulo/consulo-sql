/**
 * @author VISTALL
 * @since 08-Jul-22
 */
module consulo.sql.language.impl {
    requires transitive consulo.sql.language.api;

    requires consulo.language.editor.api;
    requires consulo.language.impl;

    exports consulo.sql.language.impl;
    exports consulo.sql.language.impl.highlight;
    exports consulo.sql.language.impl.lexer;
    exports consulo.sql.language.impl.parser;
    exports consulo.sql.language.impl.psi;
    exports consulo.sql.language.impl.version.sql92;
}