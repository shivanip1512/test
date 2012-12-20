package com.cannontech.amr.device.search.model;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class FieldValueStartsWith extends FilterByField<String> {
    
    public FieldValueStartsWith(SearchField searchField) {
        this(searchField, null);
    }

    public FieldValueStartsWith(SearchField searchField, String filterValue) {
        super(searchField, filterValue);
    }
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPPER(" + getSearchField().getQueryField() + ")");
        sql.append("LIKE UPPER(");
        sql.appendArgument(getFilterValue());
        sql.append(")");
        
        return sql;
    }
    
    @Override
    public String toSearchString() {
        return getSearchField().getFieldName() + " starts with '" + getFilterValue() + "'";
    }
}
