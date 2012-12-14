package org.featherj.db.queries.expr;

import java.util.List;

/**
 * Represents a constant value expression.
 *
 * @param <T> Value type.
 */
public class QueryVal<T> extends QueryExpr<T> {
    private T value;

    public QueryVal(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public void assembleUsageSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append("? ");
        parameterValues.add(value);
    }
}
