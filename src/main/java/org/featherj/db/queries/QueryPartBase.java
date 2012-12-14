package org.featherj.db.queries;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A base class for all different query parts.
 *
 * @param <T> Type of query that owns this part.
 */
public abstract class QueryPartBase {

    private QueryBase query;

    public <T extends QueryBase> void setOwnerQuery(T query) {
        this.query = query;
    }

    /**
     * Returns query that owns this part.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends QueryBase> T getQuery() {
        return (T) query;
    }

    public ExtendedPreparedStatement getStatement(Connection connection) throws SQLException, IOException {
        return query.getStatement(connection);
    }

    /**
     * Assembles SQL text and extracts query parameter values for further processing within {@link PreparedStatement}.
     *
     * @param parameterValues A list that will be filled with parameter values for {@link PreparedStatement}.
     * @return Assembled query text.
     */
    public abstract void assembleSql(StringBuilder sb,  List<Object> parameterValues);

    public String assembleSql(List<Object> parameterValues) {
        StringBuilder sb = new StringBuilder();
        assembleSql(sb, parameterValues);
        return sb.toString();
    }
}
