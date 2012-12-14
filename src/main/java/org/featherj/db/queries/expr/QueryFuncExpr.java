package org.featherj.db.queries.expr;

import java.util.List;

/**
 * Represents a function call expression.
 *
 * @param <T> Function result type.
 */
public class QueryFuncExpr<T> extends QueryExpr<T> {

    public enum Functions {
        Min("MIN"),
        Max("MAX"),
        Count("COUNT"),
        CountStar("COUNT(*)"),
        Sum("SUM"),
        FoundRows("FOUND_ROWS"),
        Concat("CONCAT"),
        Sqrt("SQRT");

        private String value;
        private Functions(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    private Functions function;
    private QueryExpr<?>[] args;

    public QueryFuncExpr(Functions function) {
        this.function = function;
    }

    public QueryFuncExpr(Functions function, QueryExpr<?>...args) {
        this.function = function;
        this.args = args;
    }

    @Override
    public void assembleUsageSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append(function.getValue());
        if (function != Functions.CountStar) {
            sb.append("(");
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (i != 0) {
                        sb.append(", ");
                    }
                    args[i].assembleUsageSql(sb, parameterValues);
                }
            }
            sb.append(")");
        }
        sb.append(" ");
    }

}
