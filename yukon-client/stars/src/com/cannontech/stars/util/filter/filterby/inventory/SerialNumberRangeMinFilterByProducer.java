package com.cannontech.stars.util.filter.filterby.inventory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterby.FilterBy;

public class SerialNumberRangeMinFilterByProducer extends AbstractInventoryFilterByProducer {

    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        return Arrays.asList(NON_DUMMY_METER,
                             createSerialRangeMin(filter.getFilterID()));
    }

    @Override
    public int getFilterType() {
        return YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN;
    }

    private FilterBy createSerialRangeMin(final String filterValue) {
        return new FilterBy() {
            @Override
            public Collection<JoinTable> getJoinTables() {
                return JoinTable.EMPTY_JOINTABLES;
            }
            @Override
            public String getSql() {
                return "CAST(lmhb.ManufacturerSerialNumber AS NUMERIC) >= ?";
            }
            @Override
            public List<Object> getParameterValues() {
                return Arrays.<Object>asList(filterValue);
            }
        };
    }
    
}
