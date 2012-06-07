package com.cannontech.stars.util.filter.filterBy.inventory;

import java.util.Collection;
import java.util.Collections;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.filterBy.FilterBy;

public class MemberFilterByProducer extends AbstractInventoryFilterByProducer {

    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        // The Member Filter is handled outside of the FilterProcessor
        return Collections.emptyList();
    }

    @Override
    public int getFilterType() {
        return YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_MEMBER;
    }

}
