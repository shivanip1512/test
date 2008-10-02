package com.cannontech.stars.util.filter.filterby.workorder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterby.FilterBy;

public class ServiceCompanyFilterByProducer extends AbstractWorkOrderFilterByProducer {

    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        return Arrays.asList(createServiceCompany(filter.getFilterID()));
    }

    @Override
    public int getFilterType() {
        return YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY;
    }

    private static FilterBy createServiceCompany(final String filterValue) {
        return new FilterBy() {
            @Override
            public Collection<JoinTable> getJoinTables() {
                return JoinTable.EMPTY_JOINTABLES;
            }
            @Override
            public String getSql() {
                return "wob.ServiceCompanyID = ?";
            }
            @Override
            public List<Object> getParameterValues() {
                return Arrays.<Object>asList(filterValue);
            }
        };
    }
    
}
