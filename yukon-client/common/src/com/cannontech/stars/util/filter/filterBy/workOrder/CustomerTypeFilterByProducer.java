package com.cannontech.stars.util.filter.filterBy.workOrder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterBy.FilterBy;

public class CustomerTypeFilterByProducer extends AbstractWorkOrderFilterByProducer {

    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        int filterId = Integer.parseInt(filter.getFilterID());
        return (filterId == -1) ?
            Arrays.asList(RESIDENTIAL_CUSTOMER_TYPE) :
            Arrays.asList(COMMERCIAL_CUSTOMER_TYPE,
                          createCommericalType(filter.getFilterID()));
    }

    @Override
    public int getFilterType() {
        return YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_CUST_TYPE;
    }

    private FilterBy createCommericalType(final String filterValue) {
        return new FilterBy() {
            @Override
            public Collection<JoinTable> getJoinTables() {
                return Arrays.<JoinTable>asList(WorkOrderJoinTables.CICUSTOMER);
            }
            @Override
            public String getSql() {
                return "cicb.CiCustType = ?";
            }
            @Override
            public List<Object> getParameterValues() {
                return Arrays.<Object>asList(filterValue);
            }
        };    
    };
    
}
