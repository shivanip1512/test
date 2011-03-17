package com.cannontech.stars.util.filter.filterby.workorder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterby.FilterBy;

public class PostalCodesFilterByProducer extends AbstractWorkOrderFilterByProducer {

    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        return Arrays.asList(createServiceCompanyPostalCodes(filter.getFilterText()));
    }

    @Override
    public int getFilterType() {
        return YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMP_CODES;
    }
    
    private FilterBy createServiceCompanyPostalCodes(final String filterValue) {
        return new FilterBy() {
            @Override
            public Collection<JoinTable> getJoinTables() {
                return Arrays.<JoinTable>asList(WorkOrderJoinTables.ADDRESS);
            }
            @Override
            public String getSql() {
                return "addr.ZipCode LIKE ?";
            }
            @Override
            public List<Object> getParameterValues() {
                //filterValue is formatted "Postal Code: value"
                String[] split = filterValue.split(": ");
                String value = split[1];
                return Arrays.<Object>asList(value + "%");
            }
        };
    };

}
