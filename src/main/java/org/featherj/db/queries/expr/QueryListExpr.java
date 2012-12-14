package org.featherj.db.queries.expr;

import java.util.List;

public class QueryListExpr<T> extends QueryExpr<T[]> {

    private T[] values;

    public QueryListExpr(T[] values) {
        this.values = values;
    }

    public QueryListExpr(List<T> values) {
        this.values = (T[]) values.toArray();
    }

    @Override
    public void assembleUsageSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append("(");
        for (int i = 0; i < values.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append("?");
            parameterValues.add(values[i]);
        }
        sb.append(") ");
    }
}
