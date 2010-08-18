package com.cannontech.amr.deviceread.dao.impl;

import java.util.Set;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceAttributeReadStrategy {
    
    public DeviceAttributeReadStrategyType getType();
    
    public boolean canRead(PaoType paoType);
    
    public void initiateRead(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes, DeviceAttributeReadCallback callback, DeviceRequestType type, LiteYukonUser user);

    public boolean isReadable(Iterable<? extends YukonPao> devices, Set<Attribute> attributes, LiteYukonUser user);

}
