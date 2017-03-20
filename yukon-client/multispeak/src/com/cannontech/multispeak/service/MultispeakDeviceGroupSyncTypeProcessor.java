package com.cannontech.multispeak.service;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.multispeak.client.MultispeakVendor;

public interface MultispeakDeviceGroupSyncTypeProcessor {

    public boolean processMeterSync(MultispeakVendor mspVendor, String deviceGroupSyncValue, YukonMeter yukonMeter);

    public PersistedSystemValueKey getPersistedSystemValueKey();
}
