package org.featherj.db.queries.expr;

import java.util.List;

/**
 * An aliased expression wrapper that is rendered as <code>`expr` AS `alias`</code> when expression is used in
 * e.g. SELECT or joins (in case of a table), and as just <code>`alias`</code> when it's used within another expressions.
 *
 * @param <T> Result type of the expression (wrapped).
 */
public class AliasedExpr<T> extends QueryExpr<T> {

    private String alias;
    private QueryExpr<T> expr;

    public AliasedExpr(String alias, QueryExpr<T> queryExpr) {
        this.alias = alias;
        this.expr = queryExpr;
    }

    @Override
    public void assembleDeclarationSql(StringBuilder sb, List<Object> parameterValues) {
        expr.assembleDeclarationSql(sb, parameterValues);
        sb.append("AS `" + alias + "` ");
    }

    @Override
    public void assembleUsageSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append("`" + alias + "` ");
    }

}
