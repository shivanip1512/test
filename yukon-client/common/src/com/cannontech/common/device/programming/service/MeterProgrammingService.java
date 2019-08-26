package com.cannontech.common.device.programming.service;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.model.MeterProgramUploadCancelResult;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.user.YukonUserContext;

public interface MeterProgrammingService {
	
	/**
	 * Retrieves meter programming status
	 */
	int retrieveMeterProgrammingStatus(DeviceCollection deviceCollection,
			SimpleCallback<CollectionActionResult> callback, YukonUserContext context);

	/**
	 * Cancels meter program upload
	 */
	MeterProgramUploadCancelResult cancelMeterProgramUpload(SimpleDevice device, YukonUserContext context);

	
	/**
	 * Initiates meter program upload
	 */
	int initiateMeterProgramUpload(DeviceCollection deviceCollection, SimpleCallback<CollectionActionResult> callback,
			String guid, YukonUserContext context);
}
