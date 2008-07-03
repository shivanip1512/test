package com.cannontech.common.device.groups.dao.impl.providers;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;


public abstract class FullyBinningDeviceGroupProviderBase<T> extends
        BinningDeviceGroupProviderBase<T> {

    @Override
    protected String getAllBinnedDeviceSqlSelect() {
        return "1 = 1";
    }
    
    @Override
    public boolean isDeviceInGroup(DeviceGroup base, YukonDevice device) {
        if (base instanceof BinningDeviceGroupProviderBase.BinnedDeviceGroup) {
            BinnedDeviceGroup bdg = (BinnedDeviceGroup) base;
            
            boolean inInBin = isDeviceInBin(bdg.bin, device);
            return inInBin;
        } else {
            // this must be the stored dynamic group
            return true;
        }
    }

}
