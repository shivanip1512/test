package com.cannontech.common.device.service;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PointReadService {
    public void backgroundReadPoint(PaoPointIdentifier paoPointIdentifier, CommandRequestExecutionType type, LiteYukonUser user);
}
