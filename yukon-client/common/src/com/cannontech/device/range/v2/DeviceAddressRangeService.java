package com.cannontech.device.range.v2;

import com.cannontech.common.pao.PaoType;

public interface DeviceAddressRangeService {
    
    public LongRange getAddressRangeForDevice(PaoType paoType);
    
    public boolean isValidAddress(PaoType paoType, long address);
}
