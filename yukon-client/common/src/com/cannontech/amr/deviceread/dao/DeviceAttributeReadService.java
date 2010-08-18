package com.cannontech.amr.deviceread.dao;

import java.util.Set;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceAttributeReadService {
    public void initiateRead(Iterable<? extends YukonPao> devices,
            Set<? extends Attribute> attributes, DeviceAttributeReadCallback callback,
            DeviceRequestType type, LiteYukonUser user);

    /**
     * This method will return true if at least one attribute of at least one
     * of the meters could be read in the absence of communication problems.
     * 
     * This will return false if either the devices or attributes collection is empty.
     */
    public boolean isReadable(Iterable<? extends YukonPao> devices, Set<Attribute> attributes,
            LiteYukonUser user);
}
