package org.featherj.db.queries;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.featherj.db.queries.expr.QueryBoolExpr;
import org.featherj.db.queries.expr.QueryExpr;

/**
 * Base class for all query builders. Contains getters/setters for different query parts.
 *
 */
public abstract class QueryBase extends QueryPartBase {

    /**
     * Whether to log current statement
     */
    private boolean logStatement = true;

    private ArrayList<Join> joins = new ArrayList<Join>();
    private QueryBoolExpr where;
    private QueryExpr<?>[] groupBy;
    private QueryExpr<?> orderBy;
    private QueryBoolExpr having;

    private int limitOffset;
    private int limitCount;

    public boolean isLogStatement() {
        return logStatement;
    }

    public ArrayList<Join> getJoins() {
        return joins;
    }

    public void setJoins(ArrayList<Join> joins) {
        this.joins = joins;
    }

    public QueryBoolExpr getWhere() {
        return where;
    }

    public void setWhere(QueryBoolExpr where) {
        this.where = where;
    }

    public QueryExpr<?>[] getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(QueryExpr<?>[] groupBy) {
        this.groupBy = groupBy;
    }

    public QueryExpr<?> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(QueryExpr<?> orderBy) {
        this.orderBy = orderBy;
    }

    public QueryBoolExpr getHaving() {
        return having;
    }

    public void setHaving(QueryBoolExpr having) {
        this.having = having;
    }

    public int getLimitOffset() {
        return limitOffset;
    }

    public void setLimitOffset(int limitOffset) {
        this.limitOffset = limitOffset;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    /**
     * Assembles query text and collects parameter values using {@link QueryBase#assembleSql(List)} and initializes
     * new {@link PreparedStatement} using the query text and parameter values.
     *
     * @param connection Active {@link Connection}.
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public ExtendedPreparedStatement getStatement(Connection connection) throws SQLException, IOException {
        ArrayList<Object> parameterValues = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder();
        assembleSql(sb, parameterValues);
        return prepareStatement(connection, sb, parameterValues);
    }

    public ExtendedPreparedStatement prepareStatement(Connection connection, StringBuilder sb, List<Object> parameterValues) throws SQLException, IOException {
        ExtendedPreparedStatement statement =  new ExtendedPreparedStatement(connection.prepareStatement(sb.toString()));
        int parameterIndex = 1;
        for (Object value : parameterValues) {
            statement.setObject(parameterIndex, value);
            parameterIndex++;
        }

        return statement;
    }

    public String getQueryText() {
        return assembleSql(new ArrayList<Object>());
    }
}
