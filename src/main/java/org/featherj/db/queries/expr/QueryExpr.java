package org.featherj.db.queries.expr;

import java.util.List;

import org.featherj.db.queries.QueryAssemblyUnit;
import org.featherj.db.queries.expr.QueryBinExpr.Operator;
import org.featherj.db.queries.expr.QueryFuncExpr.Functions;
import org.featherj.db.queries.expr.QuerySortExpr.SortDirection;

/**
 * Base class for all query expressions - units that can be used as "select items", within conditions or <code>VALUES</code> clause etc.
 *
 * @param <T> Result type of the expression.
 */
public abstract class QueryExpr<T> implements QueryAssemblyUnit {

    @Override
    public void assembleDeclarationSql(StringBuilder sb,  List<Object> parameterValues){
        assembleUsageSql(sb, parameterValues);
    }

    @Override
    public abstract void assembleUsageSql(StringBuilder sb, List<Object> parameterValues);

    /**
     * Returns a new {@link AliasedExpr} expression wrapped around this expression with specified alias.
     * See {@link AliasedExpr} regarding details about rendering details.
     *
     * @param alias Alias of the expression (e.g. <code>min_date</code> in case of <code>MIN(date) AS min_date</code> expression)
     * @return
     */
    public AliasedExpr<T> as(String alias) {
        return new AliasedExpr<T>(alias, this);
    }

    public QueryBoolExpr equal(QueryExpr<T> expr) {
        return new QueryBoolExpr(this, Operator.Equal, expr);
    }

    public QueryBoolExpr notEqual(QueryExpr<T> expr) {
        return new QueryBoolExpr(this, Operator.Equal, expr);
    }

    public QueryBoolExpr isNull() {
        return new QueryBoolExpr(this, Operator.IsNull);
    }

    public QueryBoolExpr isNotNull() {
        return new QueryBoolExpr(this, Operator.IsNotNull);
    }

    public QueryBoolExpr in(T[] values) {
        return new QueryBoolExpr(this, Operator.In, new QueryListExpr<T>(values));
    }

    public QueryBoolExpr in(List<T> values) {
        return new QueryBoolExpr(this, Operator.In, new QueryListExpr<T>(values));
    }

    public QueryBoolExpr like(String pattern) {
        return new QueryBoolExpr(this, Operator.Like, new QueryVal<String>(pattern));
    }

    public QueryExpr<T> mult(QueryExpr<? extends Number> expr) {
        return new QueryBinExpr<T>(this, Operator.Mult, expr);
    }

    public <U extends Number> QueryExpr<T> mult(U value) {
        return new QueryBinExpr<T>(this, Operator.Mult, new QueryVal<U>(value));
    }

    public QueryExpr<T> div(QueryExpr<T> expr) {
        return new QueryBinExpr<T>(this, Operator.Div, expr);
    }

    public <U extends Number> QueryExpr<T> div(U value) {
        return new QueryBinExpr<T>(this, Operator.Div, new QueryVal<U>(value));
    }

    public QueryExpr<T> plus(QueryExpr<T> expr) {
        return new QueryBinExpr<T>(this, Operator.Add, expr);
    }

    public <U extends Number> QueryExpr<T> plus(U value) {
        return new QueryBinExpr<T>(this, Operator.Add, new QueryVal<U>(value));
    }

    /**
     * A singleton for <code>NULL</code> value.
     */
    public static final QueryExpr<Object> Null = new QueryExpr<Object>() {
        @Override
        public void assembleUsageSql(StringBuilder sb, List<Object> parameterValues) {
            sb.append("NULL ");
        }
    };

    /**
     * Returns created {@link QueryVal} expression.
     *
     * @param <T> Type of the value.
     * @param value
     * @return
     */
    public static <T> QueryVal<T> val(T value) {
        return new QueryVal<T>(value);
    }

    /**
     * An entry point for <code>CASE...WHEN...THEN...END</code> expression.
     *
     * @param condition
     * @return
     */
    public static WhenExprPart caseWhen(QueryBoolExpr condition) {
        CaseExpr caseExpr = new CaseExpr();
        return caseExpr.when(condition);
    }

    /**
     * Constructs expression for <code>MIN</code> SQL function.
     *
     * @param <T> Return type of the function.
     * @param expr Argument passed to <code>MIN</code> function.
     * @return
     */
    public static <T> QueryFuncExpr<T> min(QueryExpr<T> expr) {
        return new QueryFuncExpr<T>(Functions.Min, expr);
    }

    /**
     * Constructs expression for <code>SUM</code> SQL function.
     *
     * @param <T> Return type of the function.
     * @param expr Argument passed to <code>SUM</code> function.
     * @return
     */
    public static <T> QueryFuncExpr<T> sum(QueryExpr<T> expr) {
        return new QueryFuncExpr<T>(Functions.Sum, expr);
    }

    /**
     * Constructs expression for <code>COUNT</code> SQL function.
     *
     * @param <T> Return type of the function.
     * @param expr Argument passed to <code>COUNT</code> function.
     * @return
     */
    public static <T> QueryFuncExpr<T> count(QueryExpr<T> expr) {
        return new QueryFuncExpr<T>(Functions.Count, expr);
    }

    /**
     * Constructs expression for <code>COUNT(*)</code> SQL function.
     *
     * @param <T> Return type of the function.
     * @return
     */
    public static <T> QueryFuncExpr<T> count() {
        return new QueryFuncExpr<T>(Functions.CountStar);
    }

    /**
     * Constructs expression for <code>FOUND_ROWS</code> SQL function.
     *
     * @param <T> Return type of the function.
     * @return
     */
    public static <T> QueryFuncExpr<T> foundRows() {
        return new QueryFuncExpr<T>(Functions.FoundRows);
    }

    /**
     * Constructs expression for <code>CONCAT</code> SQL function.
     */
    public static QueryFuncExpr<String> concat(QueryExpr...exprs) {
        return new QueryFuncExpr<String>(Functions.Concat, exprs);
    }

    public static <T> QueryFuncExpr<T> sqrt(QueryExpr<T> expr) {
        return new QueryFuncExpr<T>(Functions.Sqrt, expr);
    }

    public static <T> QuerySortExpr<T> asc(QueryExpr<T> expr) {
        return new QuerySortExpr<T>(expr, SortDirection.Asc);
    }

    public static <T> QuerySortExpr<T> desc(QueryExpr<T> expr) {
        return new QuerySortExpr<T>(expr, SortDirection.Desc);
    }
}
