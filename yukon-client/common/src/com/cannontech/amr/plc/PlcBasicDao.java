package com.cannontech.amr.plc;

import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.core.service.impl.PaoLoader;

public interface PlcBasicDao {

    /* Returns the loader for all PLC devices that have an address in the DeviceCarrierSettings table.
     * 
     * This returns more information than the PaoDao and less information than the MeterDao
     * */
    public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader();
}
