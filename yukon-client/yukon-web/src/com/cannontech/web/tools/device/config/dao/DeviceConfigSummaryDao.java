package com.cannontech.web.tools.device.config.dao;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.web.tools.device.config.model.DeviceConfigActionHistoryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;

public interface DeviceConfigSummaryDao {
    
    public enum StateSelection implements DisplayableEnum {
        ALL(List.of(ConfigState.IN_SYNC, ConfigState.OUT_OF_SYNC, ConfigState.UNCONFIRMED, ConfigState.UNREAD)),
        IN_PROGRESS(new ArrayList<>()),
        IN_SYNC(List.of(ConfigState.IN_SYNC)),
        NEEDS_UPLOAD(List.of(ConfigState.OUT_OF_SYNC, ConfigState.UNREAD)),
        NEEDS_VALIDATION(List.of(ConfigState.UNCONFIRMED));

        private List<ConfigState> states;

        private StateSelection(List<ConfigState> states) {
            this.states = states;
        }

        public List<ConfigState> getStates() {
            return states;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.configs.summary.stateSelection." + name();
        }
    }

    public enum SortBy{
        DEVICE_NAME("PaoName"),
        DEVICE_TYPE("Type"),
        DEVICE_CONFIGURATION("Name"),
        ACTION_STATUS("LastActionStatus"),
        ACTION("LastAction"),
        STATE("CurrentState"),
        START("LastActionStart"),
        END("LastActionEnd");
        
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
    List<DeviceConfigActionHistoryDetail> getDeviceConfigActionHistory(int deviceId);

    /**
     * Returns device configuration summary detail for a single device.
     */
    DeviceConfigSummaryDetail getSummaryForDevice(int deviceId);
}
