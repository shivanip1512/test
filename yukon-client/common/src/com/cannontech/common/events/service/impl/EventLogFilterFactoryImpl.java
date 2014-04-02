package com.cannontech.common.events.service.impl;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.events.model.ArgumentColumn;
import com.cannontech.common.events.model.DateFilterValue;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.model.EventLogDateFilter;
import com.cannontech.common.events.model.EventLogNumberFilter;
import com.cannontech.common.events.model.EventLogStringFilter;
import com.cannontech.common.events.model.FilterValue;
import com.cannontech.common.events.model.NumberFilterValue;
import com.cannontech.common.events.model.StringFilterValue;
import com.cannontech.common.events.service.EventLogFilterFactory;

public class EventLogFilterFactoryImpl implements EventLogFilterFactory {

    @Override
    public UiFilter<EventLog> getFilter(FilterValue filterValue, ArgumentColumn argumentColumn) {
        if (filterValue instanceof StringFilterValue){
            StringFilterValue stringFilterValue = (StringFilterValue) filterValue;

            if (StringUtils.isBlank(stringFilterValue.getFilterValue())) {
                return null;
            }
            
            return new EventLogStringFilter(stringFilterValue, argumentColumn);
        } else if (filterValue instanceof NumberFilterValue) {
            NumberFilterValue numberFilterValue = (NumberFilterValue) filterValue;

            if (numberFilterValue.getDoubleFilterValue() == null) {
                return null;
            }
            
            return new EventLogNumberFilter(numberFilterValue, argumentColumn);
        } else if (filterValue instanceof DateFilterValue) {
            DateFilterValue dateFilterValue = (DateFilterValue) filterValue;

            if (dateFilterValue.getStartDate() == null |
                dateFilterValue.getStopDate() == null) {
                return null;
            }

            return new EventLogDateFilter(dateFilterValue, argumentColumn);
        }
        
        return null;
    }
    
}