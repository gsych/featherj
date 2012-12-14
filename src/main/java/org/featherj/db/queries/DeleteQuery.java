package org.featherj.db.queries;

import java.util.List;

import org.featherj.db.DbTable;

public class DeleteQuery extends QueryBase {

    private DbTable<?>[] tables;
    private FromPart fromPart;

    public FromPart delete(DbTable<?>...tables) {
        this.tables = tables;
        fromPart = new FromPart();
        fromPart.setOwnerQuery(getQuery());
        return fromPart;
    }

    @Override
    public void assembleSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append("DELETE ");
        if (tables != null) {
            for (int i = 0; i < tables.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }

                tables[i].assembleUsageSql(sb, parameterValues);
            }
        }
        fromPart.assembleSql(sb, parameterValues);
    }
}
