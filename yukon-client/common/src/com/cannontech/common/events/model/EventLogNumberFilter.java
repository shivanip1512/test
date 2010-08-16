package com.cannontech.common.events.model;

import com.cannontech.common.bulk.filter.SqlFragmentUiFilter;
import com.cannontech.common.util.SqlBuilder;

public class EventLogNumberFilter extends SqlFragmentUiFilter<EventLog> {
    private NumberFilterValue numberFilterValue;
    private ArgumentColumn argumentColumn;
    
    public EventLogNumberFilter(NumberFilterValue numberFilterValue,
                                ArgumentColumn argumentColumn) {
        this.numberFilterValue = numberFilterValue;
        this.argumentColumn = argumentColumn;
    }

    @Override
    protected void getSqlFragment(SqlBuilder sql) {
        sql.append(argumentColumn.getColumnName()).eq(numberFilterValue.getDoubleFilterValue());
    }
}
