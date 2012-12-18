package com.cannontech.amr.device.search.model;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class FilterByField implements EditableFilterBy<String> {
    public enum Comparator {
        STARTS_WITH_IGNORE_CASE {
            @Override
            public SqlFragmentSource getWhereClauseFragment(String name, String value) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                
                sql.append("UPPER(" + name + ") LIKE UPPER (");
                sql.appendArgument(value + "%");
                sql.append(")");
                
                return sql;
            }
            
            @Override
            public String toSearchString(String name, String value) {
                return name + " starts with '" + value + "'";
            }
        },
        IN {
            @Override
            public SqlFragmentSource getWhereClauseFragment(String name, String value) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(name).append("IN (").append(value).append(")");
                return sql;
            }
            
            @Override
            public String toSearchString(String name, String value) {
                return name + " in '" + value + "'";
            }
        };
        
        public abstract SqlFragmentSource getWhereClauseFragment(String name, String value);
        public abstract String toSearchString(String name, String value);
    }
    
    private SearchField searchField;
    private String filterValue;
    private Comparator comparator;

    public FilterByField(SearchField searchField, Comparator comparator) {
        this(searchField, null, comparator);
    }

    public FilterByField(SearchField searchField, String filterValue, Comparator comparator) {
        this.searchField = searchField;
        this.filterValue = filterValue;
        this.comparator = comparator;
    }

    public SearchField getSearchField() {
        return searchField;
    }

    @Override
    public boolean hasValue() {
        return (filterValue != null);
    }

    @Override
    public String getName() {
        return searchField.getFieldName();
    }

    @Override
    public String getFormatKey() {
        return searchField.getFormatKey();
    }

    @Override
    public String getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        return comparator.getWhereClauseFragment(searchField.getQueryField(), filterValue);
    }

    @Override
    public String toSearchString() {
        return comparator.toSearchString(searchField.toString(), filterValue);
    }
}
