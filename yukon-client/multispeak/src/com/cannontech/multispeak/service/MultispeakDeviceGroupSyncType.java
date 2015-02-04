package com.cannontech.multispeak.service;

import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.Sets;

public enum MultispeakDeviceGroupSyncType implements DisplayableEnum {

	SUBSTATION_AND_BILLING_CYCLE(Sets.immutableEnumSet(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION, 
													   MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE)),
	SUBSTATION(Sets.immutableEnumSet(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION)),
	BILLING_CYCLE(Sets.immutableEnumSet(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE)),
	;
	
	private Set<MultispeakDeviceGroupSyncTypeProcessorType> processorTypes;
	
	MultispeakDeviceGroupSyncType(Set<MultispeakDeviceGroupSyncTypeProcessorType> processorTypes) {
		this.processorTypes = processorTypes;
	}
	
	public Set<MultispeakDeviceGroupSyncTypeProcessorType> getProcessorTypes() {
		return this.processorTypes;
	}
	
	@Override
	public String getFormatKey() {
		return "yukon.web.modules.adminSetup.deviceGroupSync.multispeakDeviceGroupSyncType." + this;
	}
}
