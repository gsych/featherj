package org.featherj.db.queries;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.featherj.db.DbTable;
import org.featherj.db.Field;

public class InsertQuery extends QueryBase {

    public static final int MAX_ROWS_PER_QUERY = 1000;

    private DbTable table;
    private Field<?>[] fields;
    private ValuesPart valuesPart;
    private boolean ignore;
    private boolean isReplace;

    public ValuesPart insertInto(DbTable table, Field<?>...fields) {
        this.table = table;
        this.fields = fields;
        valuesPart = new ValuesPart();
        valuesPart.setOwnerQuery(this);
        return valuesPart;
    }

    public ValuesPart insertIgnoreInto(DbTable table, Field<?>...fields) {
        ignore = true;
        return insertInto(table, fields);
    }

    public ValuesPart replaceInto(DbTable table, Field<?>...fields) {
        isReplace = true;
        return insertInto(table, fields);
    }

    public ValuesPart getValuesPart() {
        return valuesPart;
    }

    public void assembleSql(StringBuilder sb, List<Object> parameterValues) {
        assembleWithoutValues(sb, parameterValues);
        valuesPart.assembleSql(sb, parameterValues);
    }

    /**
     * Executes batch insertion preparing parameterized statements with lots of "?" within VALUES section.
     * Sends {@link MAX_ROWS_PER_QUERY} at a time, then prepares another statement and so on until there are no more value sets.
     *
     * @param connection
     * @throws SQLException
     * @throws IOException
     */
    public void executeBatch(Connection connection) throws SQLException, IOException {
        StringBuilder queryHeader = new StringBuilder();
        ArrayList<Object> initialParameters = new ArrayList<Object>();
        assembleWithoutValues(queryHeader, initialParameters);

        for (int i = 0; i < valuesPart.getValueSetsCount(); i += MAX_ROWS_PER_QUERY) {
            StringBuilder sb = new StringBuilder(queryHeader);
            ArrayList<Object> parameterValues = new ArrayList<Object>(initialParameters);

            valuesPart.assembleBatch(sb, parameterValues, i, MAX_ROWS_PER_QUERY);

            ExPreparedStatement statement = prepareStatement(connection, sb, parameterValues);
            statement.executeUpdate();
            statement.close();
        }
    }

    private void assembleWithoutValues(StringBuilder sb, List<Object> parameterValues) {
        sb.append(isReplace ? "REPLACE " : "INSERT ");
        sb.append((ignore ? "IGNORE " : "") + "INTO ");
        sb.append(table.getExprName() + " (");
        boolean first = true;
        for (Field<?> field : fields) {
            if (first) {
                first = false;
            }
            else {
                sb.append(", ");
            }
            sb.append(field.getExprName());
        }
        sb.append(") ");
    }
}
