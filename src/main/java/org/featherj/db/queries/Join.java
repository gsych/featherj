package org.featherj.db.queries;

import org.featherj.db.DbTable;
import org.featherj.db.queries.expr.QueryBoolExpr;

public class Join {

    public enum JoinType {
        Inner("INNER JOIN"),
        Left("LEFT JOIN"),
        Right("RIGHT JOIN");

        private String value;
        private JoinType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private DbTable<?> table;
    private OnPart onPart;
    private JoinType joinType;

    public Join(JoinType joinType, DbTable<?> table, QueryBoolExpr onExpr) {
        this.table = table;
        this.onPart = new OnPart(onExpr);
        this.joinType = joinType;
    }

    public Join(JoinType joinType, DbTable<?> table, OnPart onPart) {
        this.table = table;
        this.onPart = onPart;
        this.joinType = joinType;
    }

    public DbTable<?> getTable() {
        return table;
    }

    public OnPart<?> getOnPart() {
        return onPart;
    }

    public JoinType getJoinType() {
        return joinType;
    }
}
