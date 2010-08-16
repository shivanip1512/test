package com.cannontech.common.events.model;

import com.cannontech.common.bulk.filter.SqlFragmentUiFilter;
import com.cannontech.common.util.SqlBuilder;

public class EventLogDateFilter extends SqlFragmentUiFilter<EventLog> {
    private DateFilterValue dateFilterValue;
    private ArgumentColumn argumentColumn;
    
    public EventLogDateFilter(DateFilterValue dateFilterValue, ArgumentColumn argumentColumn) {
        this.dateFilterValue = dateFilterValue;
        this.argumentColumn = argumentColumn;
    }

    @Override
    protected void getSqlFragment(SqlBuilder sql) {
        sql.append(argumentColumn.getColumnName()).gte(dateFilterValue.getStartDate().toDateTimeAtStartOfDay()).append(" AND ");
        sql.append(argumentColumn.getColumnName()).lte(dateFilterValue.getStopDate().toDateTimeAtStartOfDay().plusDays(1));
    }
}
