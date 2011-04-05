package com.cannontech.device.range;

import com.cannontech.common.pao.PaoType;

public interface PlcAddressRangeService {
    
    public IntegerRange getAddressRangeForDevice(PaoType paoType);
    
    public boolean isValidAddress(PaoType paoType, int address);
}
