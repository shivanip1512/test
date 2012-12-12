package com.cannontech.amr.device.search.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.cannontech.amr.device.search.dao.DeviceSearchDao;
import com.cannontech.amr.device.search.model.DeviceSearchCategory;
import com.cannontech.amr.device.search.model.DeviceSearchFilterBy;
import com.cannontech.amr.device.search.model.DeviceSearchOrderBy;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

@Repository
public class DeviceSearchDaoImpl implements DeviceSearchDao {

    @Override
    public SearchResult<LiteYukonPAObject> search(DeviceSearchCategory category, List<DeviceSearchFilterBy> filterBy, DeviceSearchOrderBy orderBy, Boolean orderByDescending, int start, int count) {
        return getSearchResults(category, filterBy, orderBy, orderByDescending, start, count);
    }

    private List<LiteYukonPAObject> getDevices(DeviceSearchCategory category, List<DeviceSearchFilterBy> filterBy) {
        List<LiteYukonPAObject> deviceList;
        
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> allPaos = cache.getAllDevices();
            deviceList = new ArrayList<LiteYukonPAObject>(allPaos.size());
            
            for(LiteYukonPAObject lPao : allPaos) {
                if(category.contains(lPao)) {
                    boolean addDevice = true;
                    for(DeviceSearchFilterBy filter : filterBy) {
                        if(!filter.isValid(lPao)) {
                            addDevice = false;
                            break;
                        }
                    }
                    if(addDevice) deviceList.add(lPao);
                }
            }
        }
        
        return deviceList;
    }

    private SearchResult<LiteYukonPAObject> getSearchResults(DeviceSearchCategory category, List<DeviceSearchFilterBy> filterBy, DeviceSearchOrderBy orderBy, Boolean orderByDescending, int page, int itemsPerPage) {
        List<LiteYukonPAObject> deviceList = getDevices(category, filterBy);
        
        if(orderBy != null) {
            Collections.sort(deviceList, ((orderByDescending != null && orderByDescending) ? orderBy.getReverseComparator() : orderBy.getComparator()));
        }
        
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, deviceList.size());
        
        SearchResult<LiteYukonPAObject> deviceSearchResults = new SearchResult<LiteYukonPAObject>();
        deviceSearchResults.setBounds(startIndex, itemsPerPage, deviceList.size());
        deviceSearchResults.setResultList(deviceList.subList(startIndex, endIndex));
        
        return deviceSearchResults;
    }
}
