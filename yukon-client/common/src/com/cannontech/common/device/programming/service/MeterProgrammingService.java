package com.cannontech.common.device.programming.service;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.model.MeterProgramUploadCancelResult;
import com.cannontech.user.YukonUserContext;

public interface MeterProgrammingService {
	
	/**
	 * Retrieves meter programming status
	 */
	int retrieveMeterProgrammingStatus(DeviceCollection deviceCollection, YukonUserContext context);

	/**
	 * Cancels meter program upload
	 */
	MeterProgramUploadCancelResult cancelMeterProgramUpload(SimpleDevice device, YukonUserContext context);

	
	/**
	 * Initiates meter program upload
	 */
	int initiateMeterProgramUpload(DeviceCollection deviceCollection, String guid, YukonUserContext context);
}
