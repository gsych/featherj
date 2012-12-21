package org.featherj.tools;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class DbTablesGenerator extends DbBasedGenerator{

    private final String tablesOutputDir;
    private final String recordsOutputDir;
    private String basePackage;
    private Map<String, DbColumn> currentTableSchema;

    public static void main(String[] args) throws Exception {

        if (args == null || args.length != 5) {
            throw new IllegalArgumentException("Please specify exactly 5 command-line arguments: " +
                    "<connection-string> <username> <password> <src-base-dir> <relative-output-dir>.");
        }

        String connectionString = args[0];
        String username = args[1];
        String password = args[2];
        String srcBasePath = args[3];
        String relativeTargetPath = args[4];

        String outputDir = srcBasePath + File.separatorChar + relativeTargetPath;

        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", username);
        connectionProps.put("password", password);

        try {
            conn = DriverManager.getConnection(connectionString, connectionProps);
            System.out.println("Connected to database");

            DbTablesGenerator generator = new DbTablesGenerator(conn, outputDir, relativeTargetPath);
            generator.generate();
        }
        finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
    }

    public DbTablesGenerator(Connection connection, String outputDir, String relativeTargetPath) {
        super(connection);
        this.tablesOutputDir = outputDir;
        this.recordsOutputDir = outputDir + "\\records";
        this.basePackage = relativeTargetPath.replace(File.separatorChar, '.');
        if (basePackage != null && basePackage.length() > 0 && basePackage.charAt(basePackage.length() - 1) == '.') {
            basePackage = basePackage.substring(0, basePackage.length() - 1);
        }
    }

    public void generate() throws Exception {

        try {
            for (String tableName : getTableNames()) {
                currentTableSchema = queryTableScheme(tableName);
                generateTableClass(tableName);
                generateRecordClass(tableName);
            }
        }
        finally {
            if ( (getConnection() != null) && !getConnection().isClosed() ) {
                getConnection().close();
            }
        }
    }

    private void generateTableClass(String tableName) throws Exception {
        init();

        System.out.println("Generating table class for " + tableName);

        String className = camelCase(tableName);

        generateTableClassPackageAndImports();

        appendln("public class " + className + " extends DbTable<" + className + "> {");
        appendln();
        indent();
        generateSingleton(className);
        generateFields(tableName);
        generateConstructors(tableName, className);
        generateMethods(tableName, className);
        outdent();
        append("}");

        writeTableClassToFile(className);
    }

    private void generateRecordClass(String tableName) throws Exception {
        init();

        System.out.println("Generating record class for " + tableName);

        String tableClassName = camelCase(tableName);
        String className = tableClassName + "Record";

        generateRecordClassPackageAndImports(tableClassName);

        appendln("public class " + className + " extends DbRecord {");
        appendln();
        indent();
        generateRecordConstructors(tableClassName, className);
        generateRecordGettersAndSetters(tableClassName);
        outdent();
        append("}");

        writeRecordClassToFile(className);
    }

    private void generateTableClassPackageAndImports() {
        appendln("package " + basePackage + ";");

        appendln();

        appendln("import java.sql.Timestamp;");
        appendln("import org.featherj.db.DbTable;");
        appendln("import org.featherj.db.Field;");

        appendln();
    }

    private void generateRecordClassPackageAndImports(String tableClassName) {
        appendln("package " + basePackage + ".records;");

        appendln();

        appendln("import static " + basePackage + "." + tableClassName + "." + tableClassName + ";");
        appendln("import org.featherj.db.DbRecord;");
        appendln("import java.sql.Timestamp;");
        appendln();
    }

    private void generateSingleton(String className) {
        appendln("public static final " + className + " " + className + " = new " + className + "();");
        appendln();
    }

    private void generateFields(String tableName) throws Exception {
        for (String columnName : currentTableSchema.keySet()) {
            DbColumn column = currentTableSchema.get(columnName);
            String typeName = "Field<" + getColumnJavaType(column.getSqlType()) + ">";
            append("public final " + typeName + " " + camelCase(columnName));
            appendln(" = new " + typeName + "(\"" + columnName + "\", this);");
        }
        appendln();
    }

    private void generateConstructors(String tableName, String className) throws Exception {
        appendln("public " + className + "() {");
        indent();
        appendln("super(\"" + tableName + "\");");
        outdent();
        appendln("}");
        appendln();

        appendln("public " + className + "(String alias) {");
        indent();
        appendln("super(\"" + tableName + "\", alias);");
        outdent();
        appendln("}");
        appendln();
    }

    private void generateRecordConstructors(String tableClassName, String className) {
        appendln("public " + className + "() {");
        indent();
        appendln("super(" + tableClassName + ");");
        outdent();
        appendln("}");
        appendln();

        appendln("public " + className + "(DbRecord innerRecord) {");
        indent();
        appendln("super(innerRecord);");
        outdent();
        appendln("}");
        appendln();
    }

    private void generateMethods(String tableName, String className) {
        appendln("@Override");
        appendln("public " + className + " as(String alias) {");
        indent();
        appendln("return new " + className + "(alias);");
        outdent();
        appendln("}");
        appendln();
    }

    private void generateRecordGettersAndSetters(String tableClassName) throws Exception {
        for (String columnName : currentTableSchema.keySet()) {
            DbColumn column = currentTableSchema.get(columnName);
            String typeName = getColumnJavaType(column.getSqlType());
            String fieldName = camelCase(columnName);
            appendln("public " + typeName + " get" + fieldName + "() {");
            indent();
            appendln("return getValue(" + tableClassName + "." + fieldName + ");");
            outdent();
            appendln("}");
            appendln();
            appendln("public void set" + fieldName + "(" + typeName + " value) {");
            indent();
            appendln("setValue(" + tableClassName + "." + fieldName + ", value);");
            outdent();
            appendln("}");
            appendln();
        }
    }

    private static String getColumnJavaType(int sqlType) throws Exception {
        switch (sqlType) {

        case Types.BIGINT:
        case Types.BIT:
        case Types.INTEGER:
        case Types.SMALLINT:
        case Types.TINYINT:
            return "Integer";

        case Types.DECIMAL:
        case Types.DOUBLE:
        case Types.FLOAT:
        case Types.REAL:
        case Types.NUMERIC:
            return "Double";

        case Types.TIME:
        case Types.TIMESTAMP:
        case Types.DATE:
            return "Timestamp";

        case Types.CHAR:
            return "Character";

        case Types.LONGVARCHAR:
        case Types.VARCHAR:
            return "String";

        default:
            throw new Exception("Unsupported MySQL type");
        }
    }

    private void writeTableClassToFile(String className) throws IOException, URISyntaxException {
        writeToFile(className, tablesOutputDir);
    }

    private void writeRecordClassToFile(String className) throws IOException, URISyntaxException {
        writeToFile(className, recordsOutputDir);
    }

    private void writeToFile(String className, String outputDir) throws IOException, URISyntaxException {
        File dir = new File(outputDir);
        dir.mkdirs();
        FileUtils.writeStringToFile(new File(dir, className + ".java"), getCurrentClass().toString());
    }
}
