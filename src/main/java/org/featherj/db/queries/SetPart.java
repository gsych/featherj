package org.featherj.db.queries;

import java.util.ArrayList;
import java.util.List;

import org.featherj.db.Field;
import org.featherj.db.queries.expr.QueryExpr;
import org.featherj.db.queries.expr.QueryVal;

public class SetPart extends RemainderPart<SetPart> {

    private class Setter {
        private Field<?> field;
        private QueryExpr<?> expr;

        public Setter(Field<?> field, QueryExpr<?> expr) {
            this.field = field;
            this.expr = expr;
        }
    }

    private ArrayList<Setter> setters = new ArrayList<Setter>();

    public <T> SetPart set(Field<T> field, QueryExpr<T> expr) {
        setters.add(new Setter(field, expr));
        return this;
    }

    public <T> SetPart set(Field<T> field, T value) {
        set(field, new QueryVal<T>(value));
        return this;
    }

    @Override
    protected void assembleAfterJoinsBeforeWhere(StringBuilder sb, List<Object> parameterValues) {
        if (setters.size() == 0) {
            return;
        }

        sb.append("SET ");
        boolean isFirst = true;
        for (Setter setter : setters) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                sb.append(", ");
            }
            setter.field.assembleUsageSql(sb, parameterValues);
            sb.append("= ");
            setter.expr.assembleUsageSql(sb, parameterValues);
        }
    }
}
