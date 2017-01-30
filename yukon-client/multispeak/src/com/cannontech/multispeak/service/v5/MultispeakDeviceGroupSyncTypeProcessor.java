package com.cannontech.multispeak.service.v5;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.multispeak.client.MultispeakVendor;

public interface MultispeakDeviceGroupSyncTypeProcessor {

    public boolean processMeterSync(MultispeakVendor mspVendor, ElectricMeter mspMeter, YukonMeter yukonMeter);

    public PersistedSystemValueKey getPersistedSystemValueKey();
}
