package org.featherj.tools;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * An abstract parent class for generators that used database information to generate data
 */
public abstract class DbBasedGenerator {

    protected class DbColumn {
        private String name;
        private int sqlType;
        private int size;
        private String sqlTypeName;
        private boolean nullable;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setSqlType(int sqlType) {
            this.sqlType = sqlType;
        }

        public int getSqlType() {
            return sqlType;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }

        public void setSqlTypeName(String sqlTypeName) {
            this.sqlTypeName = sqlTypeName;
        }

        public String getSqlTypeName() {
            return sqlTypeName;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }

        public boolean isNullable() {
            return nullable;
        }
    }

	/**
	 * Stores a single indent text
	 */
    protected final static String INDENT = "    ";

    /**
     * Stores indent string
     */
    private String indent = "";

    /**
     * Stores text of the class currently being generated
     */
    private StringBuffer currentClass;

    /**
     * Stores DB connection instance
     */
    private Connection connection;

    protected DbBasedGenerator(Connection connection) {
        this.connection = connection;
    }


	/**
	 * The main generator method
	 * @throws Exception
	 */
	public abstract void generate() throws Exception;

	/**
	 * Returns a list of database table names
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
    protected ArrayList<String> getTableNames() throws SQLException, IOException {
        DatabaseMetaData metaData = getConnection().getMetaData();
        ResultSet result = metaData.getTables(null, null, null, null);

        ArrayList<String> tableNames = new ArrayList<String>();

        while (result.next()) {
            tableNames.add(result.getString("TABLE_NAME"));
        }

        return tableNames;
    }

    /**
     * Returns table scheme information
     *
     * @param tableName
     * @return
     * @throws SQLException
     * @throws IOException
     */
    protected HashMap<String, DbColumn> queryTableScheme(String tableName) throws SQLException, IOException {
        HashMap<String, DbColumn> tableScheme = new HashMap<String, DbColumn>();

        DatabaseMetaData metaData = getConnection().getMetaData();
        ResultSet result = metaData.getColumns(null, null, tableName, null);
        while (result.next()) {
            DbColumn column = new DbColumn();
            column.setName(result.getString("COLUMN_NAME"));
            column.setSqlType(result.getInt("DATA_TYPE"));
            column.setSize(result.getInt("COLUMN_SIZE"));
            column.setSqlTypeName(result.getString("TYPE_NAME"));
            column.setNullable("YES".equalsIgnoreCase(result.getString("IS_NULLABLE")));
            tableScheme.put(result.getString("COLUMN_NAME"), column);
        }

        return tableScheme;
    }

	/**
	 * Gets connection
	 * @return connection
	 * @throws SQLException
	 * @throws IOException
	 */
	protected Connection getConnection() throws IOException, SQLException {
		return connection;
	}

    /**
     * Fields initialization method
     */
    protected void init() {
        currentClass = new StringBuffer();
        indent = "";
    }

    protected void indent() {
        indent += INDENT;
        currentClass.append(INDENT);
    }
    protected void outdent() {
        indent = indent.substring(indent.length() - INDENT.length());
        currentClass.delete(currentClass.length() - INDENT.length(), currentClass.length());
    }

    protected void append(String s) {
        currentClass.append(s);
    }

    protected void appendln() {
        appendln("");
    }

    protected void appendln(String s) {
        currentClass.append(s + "\n" + indent);
    }

    protected static String camelCase(String s){
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts){
           camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    /**
     * Converts camelCaseString to UNDERSCORED_STRING
     * @param camelCaseString
     * @return
     */
    protected static String camelCaseToUnderscored(String camelCaseString){
    	String[] parts = camelCaseString.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    	String ret = "";
    	int partNumber = 0;
    	for (String part: parts){
    		if (partNumber != 0){
    			ret += "_";
    		}
    		ret += part.toUpperCase();
    		partNumber++;
    	}
    	return ret;
    }

    private static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

	/**
	 * Gets currentClass
	 * @return currentClass
	 */
	protected StringBuffer getCurrentClass() {
		return currentClass;
	}
}
