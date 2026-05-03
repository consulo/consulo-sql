/**
 * @author VISTALL
 * @since 2026-05-03
 */
module consulo.sql.impl.jpql {
    requires consulo.sql.language.api;
    requires consulo.sql.language.impl;
    requires consulo.language.impl;

    exports consulo.sql.language.impl.jpql;
    exports consulo.sql.language.impl.jpql.lexer;
    exports consulo.sql.language.impl.jpql.parser;
}
