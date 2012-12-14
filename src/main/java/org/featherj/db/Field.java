package org.featherj.db;

import java.util.List;

import org.featherj.db.queries.expr.QueryBinExpr;
import org.featherj.db.queries.expr.QueryBoolExpr;
import org.featherj.db.queries.expr.QueryExpr;
import org.featherj.db.queries.expr.QueryVal;

/**
 * Base class for generated fields.
 *
 * @param <T> Field type.
 */
public class Field<T> extends QueryExpr<T> {
    private String columnName;
    private DbTable table;

    public Field(String columnName, DbTable table) {
        this.columnName = columnName;
        this.table = table;
    }

    public String getColumnName() {
        return columnName;
    }

    /**
     * Returns column name quoted by "`" and prefixed by either table's alias or table's name
     * (depending on what {@link DbTable#getExprName()} returns).
     */
    public String getExprName() {
        return table.getExprName() + ".`" + getColumnName() + "`";
    }

    public QueryBoolExpr equal(Field<T> otherField) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.Equal, otherField);
    }

    public QueryBoolExpr equal(T value) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.Equal, new QueryVal<T>(value));
    }

    public QueryBoolExpr notEqual(Field<T> otherField) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.NotEqual, otherField);
    }

    public QueryBoolExpr notEqual(T value) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.NotEqual, new QueryVal<T>(value));
    }

    public QueryBoolExpr lessThan(Field<T> otherField) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.LessThan, otherField);
    }

    public QueryBoolExpr lessThan(T value) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.LessThan, new QueryVal<T>(value));
    }

    public QueryBoolExpr lessThanOrEqual(Field<T> otherField) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.LessThanOrEqual, otherField);
    }

    public QueryBoolExpr lessThanOrEqual(T value) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.LessThanOrEqual, new QueryVal<T>(value));
    }

    public QueryBoolExpr greaterThan(Field<T> otherField) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.GreaterThan, otherField);
    }

    public QueryBoolExpr greaterThan(T value) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.GreaterThan, new QueryVal<T>(value));
    }

    public QueryBoolExpr greaterThanOrEqual(Field<T> otherField) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.GreaterThanOrEqual, otherField);
    }

    public QueryBoolExpr greaterThanOrEqual(T value) {
        return new QueryBoolExpr(this, QueryBinExpr.Operator.GreaterThanOrEqual, new QueryVal<T>(value));
    }

    @Override
    public void assembleDeclarationSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append(getExprName() + " ");
    }

    @Override
    public void assembleUsageSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append(getExprName() + " ");
    }
}
