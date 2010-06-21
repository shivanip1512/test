package com.cannontech.multispeak.service;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MultispeakDeviceGroupSyncType implements DisplayableEnum {

	SUBSTATION(true, false),
	BILLING_CYCLE(false, true),
	SUBSTATION_AND_BILLING_CYCLE(true, true),
	;
	
	private boolean supportsSubstationSync;
	private boolean supportsBillingCycleSync;
	
	MultispeakDeviceGroupSyncType(boolean supportsSubstationSync, boolean supportsBillingCycleSync) {
		this.supportsSubstationSync = supportsSubstationSync;
		this.supportsBillingCycleSync = supportsBillingCycleSync;
	}
	
	public boolean isSupportsSubstationSync() {
		return supportsSubstationSync;
	}
	public boolean isSupportsBillingCycleSync() {
		return supportsBillingCycleSync;
	}
	
	@Override
	public String getFormatKey() {
		return "yukon.web.modules.multispeak.deviceGroupSync.multispeakDeviceGroupSyncType." + this;
	}
}
