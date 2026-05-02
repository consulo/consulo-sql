/**
 * @author VISTALL
 * @since 08-Jul-22
 */
module consulo.sql.language.api
{
    requires transitive consulo.language.api;

    requires transitive consulo.localize.api;

    exports consulo.sql.language;
    exports consulo.sql.language.psi;
    exports consulo.sql.language.localize;
}