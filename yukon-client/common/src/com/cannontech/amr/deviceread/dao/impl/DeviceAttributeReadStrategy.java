package com.cannontech.amr.deviceread.dao.impl;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceAttributeReadStrategy {
    
    public DeviceAttributeReadStrategyType getType();
    
    public boolean canRead(PaoType paoType);
    
    public void initiateRead(Iterable<PaoMultiPointIdentifier> points, DeviceAttributeReadStrategyCallback callback, DeviceRequestType type, LiteYukonUser user);

    public boolean isReadable(Iterable<PaoMultiPointIdentifier> points, LiteYukonUser user);

}
