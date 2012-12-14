package org.featherj.db.queries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.featherj.db.DbTable;

/**
 * An entry point for <code>SELECT</code> query.
 *
 */
public class SelectQuery extends QueryBase {

    private boolean selectAll;
    private boolean selectDistinct;
    private boolean calcFoundRows;
    private ArrayList<QueryAssemblyUnit> selectItems = new ArrayList<QueryAssemblyUnit>();
    private FromPart fromPart;

    public SelectQuery() {
        setOwnerQuery(this);
    }

    public FromPart select() {
        selectAll = true;
        return select((QueryAssemblyUnit[]) null);
    }

    /**
     * Adds specified items into the list of items to be selected within output result set.
     */
    public FromPart select(QueryAssemblyUnit...selectItems) {
        if (selectItems != null) {
            Collections.addAll(this.selectItems, selectItems);
        }
        if (fromPart == null) {
            fromPart = new FromPart();
            fromPart.setOwnerQuery(getQuery());
        }
        return fromPart;
    }

    public FromPart selectDistinct(QueryAssemblyUnit...selectItems) {
        selectDistinct = true;
        return select(selectItems);
    }

    public FromPart selectCalcFoundRows(QueryAssemblyUnit...selectItems) {
        calcFoundRows = true;
        return select(selectItems);
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public FromPart getFromPart() {
        return fromPart;
    }

    public void assembleSql(StringBuilder sb, List<Object> parameterValues) {
        sb.append("SELECT ");
        if (selectDistinct) {
            sb.append("DISTINCT ");
        }
        if (calcFoundRows) {
            sb.append("SQL_CALC_FOUND_ROWS ");
        }

        if (isSelectAll()) {
            sb.append("* ");
        }
        else if (selectItems != null) {
            appendSelectItems(sb, parameterValues);
        }

        fromPart.assembleSql(sb, parameterValues);
    }

    private void appendSelectItems(StringBuilder sb, List<Object> parameterValues) {
        boolean isFirst = true;
        for (QueryAssemblyUnit selectItem : selectItems) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                sb.append(", ");
            }

            if (selectItem instanceof DbTable) {
                sb.append(((DbTable) selectItem).getExprName() + ".*");
            }
            else {
                selectItem.assembleDeclarationSql(sb, parameterValues);
            }
        }
    }
}
