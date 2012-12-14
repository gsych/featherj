package org.featherj.db.queries;

import java.util.ArrayList;
import java.util.List;

import org.featherj.db.queries.expr.QueryExpr;

public class ValuesPart extends SelectQuery {

    private ArrayList<QueryExpr[]> valueSets = new ArrayList<QueryExpr[]>();

    public ValuesPart values(QueryExpr...values) {
        this.valueSets.add(values);
        return this;
    }

    public int getValueSetsCount() {
        return valueSets.size();
    }

    @Override
    public void assembleSql(StringBuilder sb, List<Object> parameterValues) {
        if (valueSets.size() == 0) {
            super.assembleSql(sb, parameterValues);
        }
        else {
            assembleBatch(sb, parameterValues, 0, 1);
        }
    }

    public void assembleBatch(StringBuilder sb, List<Object> parameterValues, int startRow, int count) {
        if (startRow >= valueSets.size()) {
            throw new IndexOutOfBoundsException();
        }

        sb.append("VALUES ");
        for (int i = startRow; i < startRow + count && i < valueSets.size(); i++) {
            if (i != startRow) {
                sb.append(", ");
            }
            assembleValuesRow(sb, parameterValues, valueSets.get(i));
        }
    }

    private void assembleValuesRow(StringBuilder sb, List<Object> parameterValues, QueryExpr<?>[] values) {
        sb.append("(");
        for (int i = 0; i < values.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            values[i].assembleUsageSql(sb, parameterValues);
        }
        sb.append(") ");
    }
}
