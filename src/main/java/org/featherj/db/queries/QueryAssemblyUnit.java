package org.featherj.db.queries;

import java.util.List;

/**
 * Represents a unit that can be assembled as a "declarable" SQL query part, meaning that
 * the unit can be used within "SELECT" block or within "FROM" block or joins.
 *
 */
public interface QueryAssemblyUnit {
    /**
     * Appends a SQL portion with "declaration" of the unit to the specified {@link StringBuilder}.
     * "Declaration" means that in case, for example, of an aliased column this method will append
     * <code>`column_name` AS `alias`</code> instead of just <code>`alias`</code> as in case of
     * {@link QueryAssemblyUnit#assembleUsageSql(StringBuilder, List)}.
     *
     * @param sb
     * @param parameterValues
     */
    void assembleDeclarationSql(StringBuilder sb,  List<Object> parameterValues);

    /**
     * Appends a SQL portion with "usage" of the unit to the specified {@link StringBuilder}.
     * "Usage" means that in case, for example, of an aliased column this method will append
     * <code>`alias`</code>.
     *
     * @param sb
     * @param parameterValues
     */
    void assembleUsageSql(StringBuilder sb,  List<Object> parameterValues);
}
