package com.cannontech.common.device.dao;

import java.util.List;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.device.model.DevicePointDetail;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.point.PointType;

public interface DevicePointDao {

    public enum SortBy{
        deviceName("PAOName"),
        pointName("PointName"),
        pointOffset("PointOffset"),
        pointType("PointType");
        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDbString() {
            return dbString;
        }
    }

    /**
     * 
     * Returns the RTU Point detail.
     * 
     * @param paoIds - Id of pao
     * @param pointNames - can be null
     * @param types - can be null
     * @param paging - paging information, can't be null
     * @param sortBy - used by order by, can't be null
     * @param direction - direction (asc/desc) for the order by, can't be null
     */
    
    SearchResults<DevicePointDetail> getDevicePointDetail(List<Integer> paoIds, List<String> pointNames,
            List<PointType> types, Direction direction, SortBy sortBy, PagingParameters paging);

}
