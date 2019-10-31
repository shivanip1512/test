package com.cannontech.common.device.programming.service;

import java.util.UUID;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.model.MeterProgramCommandResult;
import com.cannontech.user.YukonUserContext;

public interface MeterProgrammingService {
	
	/**
	 * Retrieves meter programming status
	 */
	int retrieveMeterProgrammingStatus(DeviceCollection deviceCollection, YukonUserContext context);
	
	/**
	 * Initiates meter program upload
	 */
	int initiateMeterProgramUpload(DeviceCollection deviceCollection, UUID guid, YukonUserContext context);

    /**
     * Retrieves meter programming status
     */
    MeterProgramCommandResult retrieveMeterProgrammingStatus(SimpleDevice device, YukonUserContext context);

    /**
     * Cancels meter program upload
     */
    MeterProgramCommandResult cancelMeterProgramUpload(SimpleDevice device, YukonUserContext context, UUID assignedGuid);

    /**
     * Initiates meter program upload again
     */
    MeterProgramCommandResult reinitiateMeterProgramUpload(SimpleDevice device, YukonUserContext context, UUID assignedGuid);

    /**
     * Accepts meter program upload again
     */
    MeterProgramCommandResult acceptMeterProgrammingStatus(SimpleDevice device, YukonUserContext context, UUID reportedGuid);

}
