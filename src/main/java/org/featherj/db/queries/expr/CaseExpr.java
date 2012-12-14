package org.featherj.db.queries.expr;

import java.util.ArrayList;
import java.util.List;

public class CaseExpr extends QueryExpr<Object> {

    private ArrayList<WhenExprPart> whenParts = new ArrayList<WhenExprPart>();
    private QueryExpr<?> elseExpr;

    public WhenExprPart when(QueryBoolExpr condition) {
        WhenExprPart whenPart = new WhenExprPart(this, condition);
        whenParts.add(whenPart);
        return whenPart;
    }

    public CaseExpr whenElse(QueryExpr<?> expr) {
        this.elseExpr = expr;
        return this;
    }

    @Override
    public void assembleUsageSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append("CASE ");
        for (WhenExprPart when : whenParts) {
            when.assembleUsageSql(sb, parameterValues);
        }
        if (elseExpr != null) {
            sb.append("ELSE ");
            elseExpr.assembleUsageSql(sb, parameterValues);
        }
        sb.append("END ");
    }
}
