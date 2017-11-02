package com.cannontech.web.tools.device.config.dao;

import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.web.tools.device.config.model.DeviceConfigActionHistory;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;

public interface DeviceConfigSummaryDao {

    public enum SortBy{
        DEVICE_NAME("DeviceName"),
        DEVICE_TYPE("DeviceType"),
        DEVICE_CONFIGURATION("ConfigName"),
        IN_SYNC("InSync"),
        ACTION_STATUS("ActionStatus"),
        ACTION("ActionStatus"),
        STATE("ExecType"),
        START("StartTime"),
        END("StopTime");
        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDbString() {
            return dbString;
        }
    }
    /**
     * Returns filtered device configuration summary detail.
     */
    SearchResults<DeviceConfigSummaryDetail> getSummary(DeviceConfigSummaryFilter filter, PagingParameters paging,
            SortBy sortBy, Direction direction);

    /**
     * Returns action (READ/SEND/VERIFY) history for device.
     */
    DeviceConfigActionHistory getDeviceConfigActionHistory(int deviceId);

    /**
     * Returns the list of device that need to be verified. Devices that have completed READ or SEND but not
     * VERIFY.
     */
    List<SimpleDevice> getDevicesToVerify();
}
