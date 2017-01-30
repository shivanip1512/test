package com.cannontech.multispeak.service.v5;

import com.cannontech.user.YukonUserContext;

public interface MultispeakDeviceGroupSyncService {
    public void startSyncForType(MultispeakDeviceGroupSyncType type, YukonUserContext userContext);
}
