package com.cannontech.web.multispeak.multispeakSync;

import org.joda.time.Instant;

import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakSyncTypeProcessorType;

public class LastRunTimestampValue {
	
	private MultispeakSyncTypeProcessorType type;
	private Instant instant;
	private boolean linkableProgress = false;
	
	public LastRunTimestampValue(MultispeakSyncTypeProcessorType type, Instant instant, MultispeakDeviceGroupSyncProgress progress) {
		
		this.type = type;
		this.instant = instant;
		if (progress != null && progress.getType().getProcessorTypes().contains(type)) {
			this.linkableProgress = true;
		}
	}
	
	public MultispeakSyncTypeProcessorType getType() {
		return type;
	}
	public Instant getInstant() {
		return instant;
	}
	public boolean isLinkableProgress() {
		return linkableProgress;
	}
}