package org.featherj.db.queries;

import java.util.List;

import org.featherj.db.queries.expr.QueryBoolExpr;

public class OnPart<T extends RemainderPart> extends QueryPartBase {

    private T parent;
    private QueryBoolExpr expr;

    public OnPart(QueryBoolExpr expr) {
        this.expr = expr;
    }

    @SuppressWarnings("unchecked")
    public OnPart(RemainderPart parent) {
        this.parent = (T) parent;
    }

    public  T on() {
        return parent;
    }

    public T on(QueryBoolExpr expr) {
        this.expr = expr;
        return parent;
    }

    public QueryBoolExpr getExpr() {
        return expr;
    }

    @Override
    public void assembleSql(StringBuilder sb, List<Object> parameterValues) {
        if (expr != null) {
            sb.append("ON ");
            expr.assembleUsageSql(sb, parameterValues);
        }
    }
}
