/**
 * @author VISTALL
 * @since 2026-05-03
 */
module consulo.sql.impl.hql {
    requires consulo.sql.language.api;
    requires consulo.sql.language.impl;
    requires consulo.sql.impl.jpql;
    requires consulo.language.impl;

    exports consulo.sql.language.impl.hql;
    exports consulo.sql.language.impl.hql.parser;
}
