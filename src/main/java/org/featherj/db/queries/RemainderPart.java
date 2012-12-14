package org.featherj.db.queries;

import java.util.List;

import org.featherj.db.DbTable;
import org.featherj.db.queries.Join.JoinType;
import org.featherj.db.queries.expr.QueryBoolExpr;
import org.featherj.db.queries.expr.QueryExpr;

/**
 * A repeatable query part that goes after e.g. <code>FROM</code> block of <code>SELECT</code> query.
 *
 */
public class RemainderPart<T extends RemainderPart> extends QueryPartBase {

    @SuppressWarnings("unchecked")
    private T getMe() {
        return (T) this;
    }

    public OnPart<T> innerJoin(DbTable table) {
        return createJoin(table, JoinType.Inner);
    }

    public OnPart<T> leftJoin(DbTable table) {
        return createJoin(table, JoinType.Left);
    }

    public T where(QueryBoolExpr expr) {
        QueryBoolExpr whereExpr = getQuery().getWhere();
        if (whereExpr != null) {
            getQuery().setWhere(whereExpr.and(expr));
        }
        else {
            getQuery().setWhere(expr);
        }
        return getMe();
    }

    public T groupBy(QueryExpr...exprs) {
        getQuery().setGroupBy(exprs);
        return getMe();
    }

    public T orderBy(QueryExpr expr) {
        getQuery().setOrderBy(expr);
        return getMe();
    }

    public T having(QueryBoolExpr expr) {
        QueryBoolExpr havingExpr = getQuery().getHaving();
        if (havingExpr != null) {
            getQuery().setHaving(havingExpr.and(expr));
        }
        else {
            getQuery().setHaving(expr);
        }
        return getMe();
    }

    public T limit(int count) {
        getQuery().setLimitCount(count);
        return getMe();
    }

    public T limit(int offset, int count) {
        getQuery().setLimitOffset(offset);
        getQuery().setLimitCount(count);
        return getMe();
    }

    @Override
    public void assembleSql(StringBuilder sb,  List<Object> parameterValues) {
        for (Join join : getQuery().getJoins()) {
            sb.append(join.getJoinType().getValue() + " ");
            join.getTable().assembleDeclarationSql(sb, parameterValues);
            join.getOnPart().assembleSql(sb, parameterValues);
        }

        assembleAfterJoinsBeforeWhere(sb, parameterValues);

        if (getQuery().getWhere() != null) {
            sb.append("WHERE ");
            getQuery().getWhere().assembleUsageSql(sb, parameterValues);
        }

        QueryExpr<?>[] groupBy = getQuery().getGroupBy();
        if (groupBy != null) {
            sb.append("GROUP BY ");
            for (int i = 0; i < groupBy.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                QueryExpr<?> groupByExpr = groupBy[i];
                groupByExpr.assembleUsageSql(sb, parameterValues);
            }
        }

        if (getQuery().getOrderBy() != null) {
            sb.append("ORDER BY ");
            getQuery().getOrderBy().assembleUsageSql(sb, parameterValues);
        }

        if (getQuery().getHaving() != null) {
            sb.append("HAVING ");
            getQuery().getHaving().assembleUsageSql(sb, parameterValues);
        }

        if (getQuery().getLimitCount() != 0) {
            sb.append("LIMIT " + (getQuery().getLimitOffset() != 0 ? getQuery().getLimitOffset() + ", " : "") + getQuery().getLimitCount());
        }
    }

    protected void assembleAfterJoinsBeforeWhere(StringBuilder sb,  List<Object> parameterValues) {

    }

    private OnPart<T> createJoin(DbTable table, JoinType type) {
        OnPart<T> onPart = new OnPart<T>(this);
        onPart.setOwnerQuery(getQuery());
        Join join = new Join(type, table, onPart);
        getQuery().getJoins().add(join);
        return onPart;
    }
}
