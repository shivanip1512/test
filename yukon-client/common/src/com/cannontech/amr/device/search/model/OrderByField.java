package com.cannontech.amr.device.search.model;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class OrderByField {
    private boolean descending = false;
    private SearchField searchField;

    public OrderByField(SearchField searchField, boolean descending) {
        this.searchField = searchField;
        this.descending = descending;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public SearchField getField() {
        return searchField;
    }

    public void setField(SearchField searchField) {
        this.searchField = searchField;
    }

    public SqlFragmentSource getSqlOrderByClause() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append(searchField.getQueryField());
        if(descending) {
            sql.append("DESC");
        }
        
        return sql;
    }
}
