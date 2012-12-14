package org.featherj.db.queries.expr;

import java.util.List;

public class WhenExprPart extends QueryExpr<Object> {

    private CaseExpr parent;
    private QueryExpr<?> thenExpr;
    private QueryBoolExpr whenCondition;

    public WhenExprPart(CaseExpr parent, QueryBoolExpr whenCondition) {
        this.parent = parent;
        this.whenCondition = whenCondition;
    }

    public CaseExpr then(QueryExpr<?> expr) {
        thenExpr = expr;
        return parent;
    }

    public <T> CaseExpr then(T value) {
        thenExpr = new QueryVal<T>(value);
        return parent;
    }

    @Override
    public void assembleUsageSql(StringBuilder sb,  List<Object> parameterValues) {
        sb.append("WHEN ");
        whenCondition.assembleUsageSql(sb, parameterValues);
        sb.append("THEN ");
        thenExpr.assembleUsageSql(sb, parameterValues);
    }
}
