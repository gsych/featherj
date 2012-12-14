package org.featherj.db.queries;

import java.util.List;

import org.featherj.db.DbTable;

public class UpdateQuery extends QueryBase {

    private DbTable<?>[] tables;
    private SetPart setPart;

    public SetPart update(DbTable<?>...tables) {
        this.tables = tables;
        setPart = new SetPart();
        setPart.setOwnerQuery(this);
        return setPart;
    }

    @Override
    public void assembleSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append("UPDATE ");
        for (int i = 0; i < tables.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            tables[i].assembleDeclarationSql(sb, parameterValues);
        }

        setPart.assembleSql(sb, parameterValues);
    }

}
