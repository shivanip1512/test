package com.cannontech.multispeak.service;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;


public interface MultispeakDeviceGroupSyncTypeProcessor {
	
	public boolean processMeterSync(MultispeakVendor mspVendor, ServiceLocation mspServiceLocation, Meter mspMeter, YukonMeter yukonMeter);
	public PersistedSystemValueKey getPersistedSystemValueKey();
}
