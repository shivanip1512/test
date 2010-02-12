package com.cannontech.common.device.groups.dao.impl.providers;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;

public abstract class CompleteBinningDeviceGroupProviderBase<T> extends
        BinningDeviceGroupProviderBase<T> {
    
    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        return new SimpleSqlFragment("SELECT d.deviceid FROM Device d");
    }
    
    @Override
    public boolean doesGroupDefinitelyContainAllDevices(DeviceGroup group) {
        if (group instanceof BinningDeviceGroupProviderBase.BinnedDeviceGroup) {
            return false;
        } else {
            return true;
        }
    }
}
