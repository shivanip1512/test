package com.cannontech.amr.device.search.model;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class FieldValueIn extends FilterByField<Iterable<?>> {
    
    public FieldValueIn(SearchField searchField, Iterable<?> filterValue) {
        super(searchField, filterValue);
    }
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(getSearchField().getQueryField()).in(getFilterValue());
        return sql;
    }
    
    @Override
    public String toSearchString() {
        return getName() + " in '" + getFilterValue() + "'";
    }
}
