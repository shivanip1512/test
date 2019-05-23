package com.cannontech.common.device.data.collection.dao;

import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public interface RecentPointValueDao {
    
    enum RangeType {
        AVAILABLE("#093"),
        EXPECTED("#4d90fe"),
        OUTDATED("#ec971f"),
        UNAVAILABLE("#888");

        private final String baseKey = "yukon.web.modules.amr.dataCollection.detail.rangeType.";

        private String color;

        private RangeType(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }

        public String getLabelKey() {
            return baseKey + this.name();
        }

    }
    
    public enum SortBy{
        DEVICE_NAME("ypo.PAOName"),
        METER_NUMBER("dmg.MeterNumber"),
        DEVICE_TYPE("ypo.Type"),
        SERIAL_NUMBER_ADDRESS("SerialNumberAddress"),
        PRIMARY_GATEWAY("GatewayName"),
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
     * If timestamp is more recent then timestamp in the table for the point id, updates the data in the
     * table with more recent values.
     * 
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
     * @param selectedGatewayIds - can be null, otherwise contains list of gatewayId's to filter with
     * @param ranges - time periods,  can't be null
     * @param paging - paging information, can't be null
     * @param sortBy - used by order by, can't be null
     * @param direction - direction (asc/desc) for the order by, can't be null
     * @return
     */
  
    SearchResults<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, List<DeviceGroup> groups,
            boolean includeDisabled, Integer[] selectedGatewayIds, Map<RangeType, Range<Instant>> ranges,
            PagingParameters paging, SortBy sortBy, Direction direction);
    
    /**
     * Returns the RfnGateway List
     * 
     * @param group - finds only devices within the group and its subgroups
     * @param groups - can be null, otherwise finds devices that are in "group" and in "groups"
     * @param includeDisabled - if false only enabled devices will be returned
     * @return
     */
    List<RfnGateway> getRfnGatewayList(DeviceGroup group, List<DeviceGroup> groups, boolean includeDisabled);

    /**
     * Returns device count for range and group.
     */
    int getDeviceCount(DeviceGroup group, boolean includeDisabled, Integer[] selectedGatewayIds, RangeType type, Range<Instant> range);
}
