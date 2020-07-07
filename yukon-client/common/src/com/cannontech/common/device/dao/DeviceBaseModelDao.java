package com.cannontech.common.device.dao;

import java.util.List;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.model.Direction;

public interface DeviceBaseModelDao<T extends DeviceBaseModel> {
    
    public enum SortBy {
        PAO_NAME("PAOName"),
        DISABLE_FLAG("DisableFlag");
        
        private final String dbString;
        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        public String getDbString() {
            return this.dbString;
        }
    }
    
    public List<DeviceBaseModel> listDevices(SortBy sortColumn, Direction direction);
}
