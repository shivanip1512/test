package com.cannontech.stars.util.filter.filterBy.inventory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterBy.FilterBy;

public class WarehouseFilterByProducer extends AbstractInventoryFilterByProducer {

    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        int filterId = Integer.parseInt(filter.getFilterID());
        return (filterId == 0) ?
                Arrays.asList(NOT_IN_WAREHOUSE) :
                Arrays.asList(createWarehouse(filter.getFilterID()));
    }

    @Override
    public int getFilterType() {
        return YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_WAREHOUSE;
    }
    
    private FilterBy createWarehouse(final String filterValue) {
        return new FilterBy() {
            @Override
            public Collection<JoinTable> getJoinTables() {
                return Arrays.<JoinTable>asList(InventoryJoinTable.INVENTORY_TO_WAREHOUSE_MAPPING);
            }
            @Override
            public String getSql() {
                return "itwhm.WarehouseID = ?";
            }
            @Override
            public List<Object> getParameterValues() {
                return Arrays.<Object>asList(filterValue);
            }
        };
    };

}
