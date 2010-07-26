package com.cannontech.common.events.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class EventLogDateFilter implements UiFilter<EventLog> {
    private DateFilterValue dateFilterValue;
    private ArgumentColumn argumentColumn;
    
    public EventLogDateFilter(DateFilterValue dateFilterValue, ArgumentColumn argumentColumn) {
        this.dateFilterValue = dateFilterValue;
        this.argumentColumn = argumentColumn;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
        retVal.add(new SqlFilter(){

            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append(argumentColumn.columnName).gte(dateFilterValue.startDate).append(" AND ");
                retVal.append(argumentColumn.columnName).lte(dateFilterValue.stopDate);
                
                return retVal;
            }});

        return retVal;
    }
    
    @Override
    public List<PostProcessingFilter<EventLog>> getPostProcessingFilters() {
        return null;
    }

}
