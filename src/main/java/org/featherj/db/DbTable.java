package org.featherj.db;

import java.util.List;

import org.featherj.db.queries.QueryAssemblyUnit;

/**
 * Base class for generated table classes.
 *
 */
public abstract class DbTable<T extends DbTable> implements QueryAssemblyUnit {
    private String tableName;
    private String alias;

    public DbTable(String tableName) {
        this.tableName = tableName;
    }

    public DbTable(String tableName, String alias) {
        this.tableName = tableName;
        this.alias = alias;
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * Returns alias of the table instance obtained through {@link DbTable#as(String)} method call.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Returns table's alias if it was initialized or table name otherwise. Return value is quoted using "`".
     * @return
     */
    public String getExprName() {
        return "`" + (getAlias() != null ? getAlias() : getTableName()) + "`";
    }

    /**
     * Creates a new instance of the table initialized with specified alias.
     *
     * @param alias
     * @return
     */
    public abstract T as(String alias);

    @Override
    public void assembleDeclarationSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append("`" + getTableName() + "`" + (getAlias() != null ? " `" + getAlias() + "`" : "") + " ");
    }

    @Override
    public void assembleUsageSql(StringBuilder sb, List<Object> parameterValues) {
        if (getAlias() != null) {
            sb.append("`" + getAlias() + "` ");
        }
        else {
            sb.append("`" + getTableName() + "` ");
        }
    }
}
