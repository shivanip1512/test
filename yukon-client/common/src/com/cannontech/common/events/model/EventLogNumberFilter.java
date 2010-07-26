package com.cannontech.common.events.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class EventLogNumberFilter implements UiFilter<EventLog> {
    private NumberFilterValue numberFilterValue;
    private ArgumentColumn argumentColumn;
    
    public EventLogNumberFilter(NumberFilterValue numberFilterValue,
                                ArgumentColumn argumentColumn) {
        this.numberFilterValue = numberFilterValue;
        this.argumentColumn = argumentColumn;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
        retVal.add(new SqlFilter(){

            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append(argumentColumn.columnName).eq(numberFilterValue.doubleFilterValue);
                
                return retVal;
            }});

        return retVal;
    }
    
    @Override
    public List<PostProcessingFilter<EventLog>> getPostProcessingFilters() {
        return null;
    }

}
