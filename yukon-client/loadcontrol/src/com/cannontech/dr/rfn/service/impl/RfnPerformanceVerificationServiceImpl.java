package com.cannontech.dr.rfn.service.impl;

import static com.cannontech.system.GlobalSettingType.RF_BROADCAST_PERFORMANCE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Range;
import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.impl.RfCommandStrategy;
import com.cannontech.system.PreferenceOnOff;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

public class RfnPerformanceVerificationServiceImpl implements RfnPerformanceVerificationService {
    private static final Logger log = YukonLogManager.getLogger(RfnPerformanceVerificationServiceImpl.class);
    
    @Autowired private RfCommandStrategy rfCommandStrategy;
    @Autowired private PerformanceVerificationDao performanceVerificationDao;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private GlobalSettingDao globalSettingDao;
    
    @Override
    @Transactional
    public void sendPerformanceVerificationMessage() {
        PreferenceOnOff preference = globalSettingDao.getEnum(RF_BROADCAST_PERFORMANCE, PreferenceOnOff.class);
        
        if (preference == PreferenceOnOff.ON) {
            try {
                log.debug("Using broadcast messaging for performance verification command.");

                // Create the event.
                PerformanceVerificationEventMessage verificationEvent = performanceVerificationDao.createVerificationEvent();
                
                // Log the enrolled devices we care about.
                performanceVerificationDao.writeNewVerificationEventForEnrolledDevices(verificationEvent.getMessageId());
                
                LmCommand command = new LmCommand();
                command.setType(LmHardwareCommandType.PERFORMANCE_VERIFICATION);
                command.getParams().put(LmHardwareCommandParam.UNIQUE_MESSAGE_ID, verificationEvent.getMessageId());
                
                rfCommandStrategy.sendBroadcastCommand(command);
            } catch (Exception e) {
                log.error("Error occurred during the sending of the RFN performance verification message.", e);
            }
        } else {
            log.debug("RF Broadcast Performance is disabled, not sending broadcast performance verification message.");
        }
    }
    
    @Override
    public Map<Integer, AssetAvailabilityStatus> getAssetAvailabilityForUnknown(long messageId) {
        Set<Integer> deviceIdsUnkown = performanceVerificationDao.getDevicesWithUnknownStatus(messageId);
        return assetAvailabilityService.getAssetAvailabilityStatus(deviceIdsUnkown);
    }
    
    @Override
    public void processVerificationMessages(int deviceId, Map<Long, Instant> verificationMsgs, Range<Instant> range){
		/*
		 * 1. Remove invalid messages
		 * 2. Add messages received from unenrolled devices
		 * (SUCCESS_UNENROLLED) 
		 * 3. Mark received messages as success (SUCCESS)
		 * 4. Check if there was a broadcast event that happened between
		 * earliest relay start time and time of reading (UTC) and the result is
		 * still "UNKNOWN". Mark the result. (UNSUCCESS)
		 */
    	//message ids that were sent to the device
		List<Long> sentMsgIds = performanceVerificationDao
				.getValidEventIds(new ArrayList<Long>(verificationMsgs.keySet()));
		//remove message ids that were never sent to the device
		Map<Long, Instant> successMsgs = Maps.filterKeys(verificationMsgs,
				Predicates.in(sentMsgIds));
    	
    	//SUCCESS_UNENROLLED
    	    	
    	List<Long> unexpectedMsgIds = new ArrayList<Long>(successMsgs.keySet());
    	// message ids we expect to receive from this device
		List<Long> expectedMsgIds = performanceVerificationDao.getValidEventIdsForDevice(
				deviceId, new ArrayList<Long>(successMsgs.keySet()));
		// Remove the message ids we expected to receive for this device. The
		// remaining message ids we didn't expect to get back because the device
		// was not enrolled in the program at the time the message was sent.
		unexpectedMsgIds.removeAll(expectedMsgIds);
		// keep track of successful responses for unenrolled devices
		for(Long eventId: unexpectedMsgIds){
			performanceVerificationDao.createUnenrolledEventResultStatus(deviceId, eventId, successMsgs.get(eventId));
		}
		
		//SUCCESS
	
		// update only expected messages as a success
		Map<Long, Instant> successMsgsToMarkAsSuccess = Maps.filterKeys(successMsgs, Predicates.in(expectedMsgIds));
		if(!successMsgsToMarkAsSuccess.isEmpty()){
			performanceVerificationDao.setEventResultStatusToSuccessful(deviceId, successMsgsToMarkAsSuccess);
		}
		
		//UNSUCCESS
		
		if(range != null){
			performanceVerificationDao.setEventResultStatusToUnuccessful(deviceId, range);
		}
    }
}