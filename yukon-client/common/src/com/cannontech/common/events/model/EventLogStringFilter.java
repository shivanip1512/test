package com.cannontech.common.events.model;

import com.cannontech.common.bulk.filter.SqlFragmentUiFilter;
import com.cannontech.common.util.SqlBuilder;

public class EventLogStringFilter extends SqlFragmentUiFilter<EventLog> {
    private StringFilterValue stringFilterValue;
    private ArgumentColumn argumentColumn;
    
    public EventLogStringFilter(StringFilterValue stringFilterValue,
                                ArgumentColumn argumentColumn) {
        this.stringFilterValue = stringFilterValue;
        this.argumentColumn = argumentColumn;
    }

    @Override
    protected void getSqlFragment(SqlBuilder sql) {
        sql.append(argumentColumn.getColumnName()).eq(stringFilterValue.getFilterValue());
    }
}
