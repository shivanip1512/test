package com.cannontech.common.events.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class EventLogStringFilter implements UiFilter<EventLog> {
    private StringFilterValue stringFilterValue;
    private ArgumentColumn argumentColumn;
    
    public EventLogStringFilter(StringFilterValue stringFilterValue,
                                ArgumentColumn argumentColumn) {
        this.stringFilterValue = stringFilterValue;
        this.argumentColumn = argumentColumn;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
        retVal.add(new SqlFilter(){

            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append(argumentColumn.columnName).eq(stringFilterValue.filterValue);
                
                return retVal;
            }});

        return retVal;
    }
    
    @Override
    public List<PostProcessingFilter<EventLog>> getPostProcessingFilters() {
        return null;
    }

}
