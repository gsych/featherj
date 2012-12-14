package org.featherj.db.queries.expr;

/**
 * A boolean expression (a series of expressions with boolean result separated by <code>AND</code> and <code>OR</code>).
 *
 */
public class QueryBoolExpr extends QueryBinExpr<Boolean> {

    public QueryBoolExpr(QueryExpr<?> expr, Operator op) {
        super(expr, op, null);
    }

    public QueryBoolExpr(QueryExpr<?> left, QueryBinExpr.Operator op, QueryExpr<?> right) {
        super(left, op, right);
    }

    public QueryBoolExpr and(QueryBoolExpr expr) {
        return new QueryBoolExpr(this, Operator.And, expr);
    }

    public QueryBoolExpr or(QueryBoolExpr expr) {
        return new QueryBoolExpr(this, Operator.Or, expr);
    }
}
