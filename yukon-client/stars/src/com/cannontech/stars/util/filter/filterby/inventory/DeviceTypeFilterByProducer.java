package com.cannontech.stars.util.filter.filterby.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterby.FilterBy;

public class DeviceTypeFilterByProducer extends AbstractInventoryFilterByProducer {

    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        List<FilterBy> filterList = new ArrayList<FilterBy>();
        filterList.add(createDeviceType(filter.getFilterID()));
        
        boolean isMCT = Integer.valueOf(filter.getFilterID()) == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT;
        if (isMCT) return filterList;
        
        filterList.add(0, NON_DUMMY_METER);
        return filterList;
    }
    
    @Override
    public int getFilterType() {
        return YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE;
    }
    
    private FilterBy createDeviceType(final String filterValue) {
        return new FilterBy() {
            @Override
            public Collection<JoinTable> getJoinTables() {
                return JoinTable.EMPTY_JOINTABLES;
            }
            @Override
            public String getSql() {
                return "lmhb.LMHardwareTypeID = ?";
            }
            @Override
            public List<Object> getParameterValues() {
                return Arrays.<Object>asList(filterValue);
            }
        };
    }

}
