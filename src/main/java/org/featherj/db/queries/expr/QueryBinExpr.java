package org.featherj.db.queries.expr;

import java.util.List;

/**
 * Represents a binary expression.
 *
 * @param <T> Result type of the expression.
 */
public class QueryBinExpr<T> extends QueryExpr<T> {

    public enum Operator {
        And("AND"),
        Or("OR"),
        Equal("="),
        NotEqual("<>"),
        LessThan("<"),
        GreaterThan(">"),
        LessThanOrEqual("<="),
        GreaterThanOrEqual(">="),
        IsNull("IS NULL"),
        IsNotNull("IS NOT NULL"),
        Add("+"),
        Sub("-"),
        Mult("*"),
        Div("/"),
        Like("LIKE"),
        In("IN");

        private String value;

        private Operator(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    private QueryExpr<?> left;
    private Operator op;
    private QueryExpr<?> right;

    public QueryBinExpr(QueryExpr<?> left, Operator op, QueryExpr<?> right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public void assembleUsageSql(StringBuilder sb,  List<Object> parameterValues) {
        boolean addParen = op == Operator.Add || op == Operator.Sub || op == Operator.Or;

        if (addParen) {
            sb.append("(");
        }
        left.assembleUsageSql(sb, parameterValues);
        sb.append(op.getValue() + " ");
        if (right != null) {
            right.assembleUsageSql(sb, parameterValues);
            if (addParen) {
                sb.append(") ");
            }
        }
    }
}
