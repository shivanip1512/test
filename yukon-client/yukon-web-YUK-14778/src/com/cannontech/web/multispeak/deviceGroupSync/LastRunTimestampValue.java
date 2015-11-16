package com.cannontech.web.multispeak.deviceGroupSync;

import org.joda.time.Instant;

import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncTypeProcessorType;

public class LastRunTimestampValue {
	
	private MultispeakDeviceGroupSyncTypeProcessorType type;
	private Instant instant;
	private boolean linkableProgress = false;
	
	public LastRunTimestampValue(MultispeakDeviceGroupSyncTypeProcessorType type, Instant instant, MultispeakDeviceGroupSyncProgress progress) {
		
		this.type = type;
		this.instant = instant;
		if (progress != null && progress.getType().getProcessorTypes().contains(type)) {
			this.linkableProgress = true;
		}
	}
	
	public MultispeakDeviceGroupSyncTypeProcessorType getType() {
		return type;
	}
	public Instant getInstant() {
		return instant;
	}
	public boolean isLinkableProgress() {
		return linkableProgress;
	}
}