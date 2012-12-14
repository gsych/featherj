package org.featherj.db.queries;

import java.util.List;

import org.featherj.db.DbTable;

/**
 * SQL <code>FROM</code> clause of {@link SelectQuery}.
 *
 */
public class FromPart extends QueryPartBase {

    private DbTable<?> table;
    private RemainderPart<?> remainderPart;

    public RemainderPart from(DbTable<?> table) {
        this.table = table;
        if (remainderPart == null) {
            remainderPart = new RemainderPart();
            remainderPart.setOwnerQuery(getQuery());
        }
        return remainderPart;
    }

    public RemainderPart getRemainderPart() {
        return remainderPart;
    }

    @Override
    public void assembleSql(StringBuilder sb, List<Object> parameterValues) {
        if (table != null) {
            sb.append("FROM ");
            table.assembleDeclarationSql(sb, parameterValues);
            remainderPart.assembleSql(sb, parameterValues);
        }
    }
}
