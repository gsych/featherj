package org.featherj.db.queries.expr;

import java.util.List;

public class QuerySortExpr<T> extends QueryExpr<T> {

    public enum SortDirection {
        Asc("ASC"),
        Desc("DESC");

        private String value;

        private SortDirection(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    private QueryExpr<T> expr;
    private SortDirection sortDirection;

    public QuerySortExpr(QueryExpr<T> expr, SortDirection sortDirection) {
        this.expr = expr;
        this.sortDirection = sortDirection;
    }

    @Override
    public void assembleUsageSql(StringBuilder sb, List<Object> parameterValues) {
        expr.assembleUsageSql(sb, parameterValues);
        sb.append(sortDirection.getValue() + " ");
    }
}
