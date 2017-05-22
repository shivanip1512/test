package com.cannontech.common.device.data.collection.dao;

import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public interface RecentPointValueDao {
    
    enum RangeType {
        AVAILABLE, EXPECTED, OUTDATED, UNAVAILABLE
    }
    
    public enum SortBy{
        DEVICE_NAME("ypo.PAOName"),
        METER_NUMBER("dmg.MeterNumber"),
        DEVICE_TYPE("ypo.Type"),
        SERIAL_NUMBER_ADDRESS("SerialNumberAddress"),
        TIMESTAMP("Timestamp"),
        ROUTE("Route");
        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDbString() {
            return dbString;
        }
    }
    /**
     * Deletes all the data from RecentPointValue.
     * Inserts recent values.
     * @param recentValues - values to insert
     */
    void collectData(Map<PaoIdentifier, PointValueQualityHolder> recentValues);

    /**
     * 
     * Returns the device collection detail.
     *  
     * @param group - finds only devices within the group and its subgroups
     * @param groups - can be null, otherwise finds devices that are in "group" and in "groups"
     * @param includeDisabled - if false only enabled devices will be returned
     * @param ranges - time periods,  can't be null
     * @param paging - paging information, can't be null
     * @param sortBy - used by order by, can't be null
     * @param direction - direction (asc/desc) for the order by, can't be null
     * @return
     */
  
    SearchResults<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, List<DeviceGroup> groups,
            boolean includeDisabled, Map<RangeType, Range<Instant>> ranges, PagingParameters paging, SortBy sortBy,
            Direction direction);

    /**
     * Returns device count for range and group.
     */
    int getDeviceCount(DeviceGroup group, boolean includeDisabled, RangeType type, Range<Instant> range);
}
