package com.cannontech.stars.util.filter.filterBy.inventory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterBy.FilterBy;

public class ApplianceTypeFilterByProducer extends AbstractInventoryFilterByProducer {

    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        return Arrays.asList(NON_DUMMY_METER,
                             createApplianceType(filter.getFilterID()));
    }

    @Override
    public int getFilterType() {
        return YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_APPLIANCE_TYPE;
    }
    
    private FilterBy createApplianceType(final String filterValue) {
        return new FilterBy() {
            @Override
            public Collection<JoinTable> getJoinTables() {
                return Arrays.<JoinTable>asList(InventoryJoinTable.APPLIANCE_BASE);
            }
            @Override
            public String getSql() {
                return "ApplianceBase.ApplianceCategoryID = ?";
            }
            @Override
            public List<Object> getParameterValues() {
                return Arrays.<Object>asList(filterValue);
            }
        };
    }

}
