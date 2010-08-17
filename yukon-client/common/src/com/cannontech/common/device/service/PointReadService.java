package com.cannontech.common.device.service;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PointReadService {
    public void backgroundReadPoint(PaoPointIdentifier paoPointIdentifier, DeviceRequestType type, LiteYukonUser user);
}
