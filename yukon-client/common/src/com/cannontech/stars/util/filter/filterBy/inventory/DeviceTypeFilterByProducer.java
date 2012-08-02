package com.cannontech.stars.util.filter.filterBy.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterBy.FilterBy;

public class DeviceTypeFilterByProducer extends AbstractInventoryFilterByProducer {

	InventoryDao inventoryDao;
	
    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        List<FilterBy> filterList = new ArrayList<FilterBy>();
        
        int filterId = Integer.parseInt(filter.getFilterID());

        int defId = inventoryDao.getYukonDefinitionIdByEntryId(filterId);
        
        boolean isMCT = defId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER;
        if (isMCT) {
        	filterList.add(0, MCT_METER);
        	return filterList;
        } else {
        	filterList.add(createDeviceType(filter.getFilterID()));
        	filterList.add(0, NON_DUMMY_METER);
        	return filterList;
		}
        
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
    
    @Autowired
    public void setInvetoryDao( InventoryDao inventoryDao) {
    	this.inventoryDao = inventoryDao;
    }

}
