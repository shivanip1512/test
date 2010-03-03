package com.cannontech.common.device.groups.dao.impl.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.db.device.Device;

public abstract class CompleteBinningDeviceGroupProviderBase<T> extends
        BinningDeviceGroupProviderBase<T> {
    
    private PaoDao paoDao;
    
    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT deviceId");
        sql.append("FROM Device");
        sql.append("WHERE deviceid").neq(Device.SYSTEM_DEVICE_ID);
        
        return sql;
    }
    
    @Override
    public boolean doesGroupDefinitelyContainAllDevices(DeviceGroup group) {
        if (group instanceof BinningDeviceGroupProviderBase.BinnedDeviceGroup) {
            return false;
        } else {
            return true;
        }
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
