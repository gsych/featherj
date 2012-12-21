package org.featherj.db;

import org.featherj.db.queries.ExResultSet;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

public class DbRecord {
    private DbTable table;
    private HashMap<String, Object> values = new HashMap<String, Object>();

    public DbRecord() {
    }

    public DbRecord(DbTable table) {
        this.table = table;
    }

    public DbRecord(DbRecord innerRecord) {
        values = innerRecord.values;
        table = innerRecord.table;
    }

    public void readFrom(ExResultSet result) throws SQLException {
        ResultSetMetaData meta = result.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
//            Field<?> field = table.getFields().get(meta.getColumnName(i));
//            if (field != null) {
//                setValue(field, result.get(field));
//            }
            setValue(meta.getColumnName(i), result.getObject(i));
        }
    }

    public void setValue(Field<?> field, Object value) {
        values.put(field.getColumnName(), value);
    }

    public <T> T getValue(Field<T> field) {
        return (T) values.get(field.getColumnName());
    }

    private void setValue(String columnName, Object value) {
        values.put(columnName, value);
    }
}
