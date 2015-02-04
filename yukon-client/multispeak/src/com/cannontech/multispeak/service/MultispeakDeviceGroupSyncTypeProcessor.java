package com.cannontech.multispeak.service;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.ServiceLocation;

public interface MultispeakDeviceGroupSyncTypeProcessor {
	
	public boolean processMeterSync(MultispeakVendor mspVendor, ServiceLocation mspServiceLocation, Meter mspMeter, YukonMeter yukonMeter);
	public PersistedSystemValueKey getPersistedSystemValueKey();
}
