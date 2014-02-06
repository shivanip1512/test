package com.cannontech.dr.rfn.service.impl;

import static com.cannontech.system.GlobalSettingType.RF_BROADCAST_PERFORMANCE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
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
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private ScheduledExecutor scheduledExecutor;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    
    private final long SECONDS_PER_DAY = 24 * 60 * 60;
    
    @Override
    @Transactional
    public void schedulePerformanceVerificationMessaging() {
        long delay = 0; // TODO: Calculate this using the time in the database.
        
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                PreferenceOnOff preference = globalSettingDao.getEnum(RF_BROADCAST_PERFORMANCE, PreferenceOnOff.class);
                
                if (preference == PreferenceOnOff.ON) {
                    try {
                        log.debug("Using broadcast messaging for performance verification command.");
    
                        Set<Integer> deviceIds = enrollmentDao.getEnrolledDevicesByTypes(PaoType.getRfLcrTypes());
                        
                        if (!deviceIds.isEmpty()) {
                            // Create the event.
                            PerformanceVerificationEventMessage verificationEvent = performanceVerificationDao.createVerificationEvent();
                            
                            // Log the enrolled devices we care about.
                            performanceVerificationDao.writeNewVerificationEventForDevices(verificationEvent.getMessageId(), deviceIds);
                            
                            LmCommand command = new LmCommand();
                            command.setType(LmHardwareCommandType.PERFORMANCE_VERIFICATION);
                            command.getParams().put(LmHardwareCommandParam.UNIQUE_MESSAGE_ID, verificationEvent.getMessageId());
                            
                            rfCommandStrategy.sendBroadcastCommand(command);
                        } else {
                            log.info("No enrolled devices exist for the performance verification event. No message will be broadcast.");
                        }
                    } catch (Exception e) {
                        // something here.
                        log.error("Error occurred during the sending of the RFN performance verification message.", e);
                    }
                } else {
                    // Worth?
                    log.debug("RF Broadcast Performance is disabled, not sending broadcast performance verification message.");
                }
            }
        }, delay, SECONDS_PER_DAY, TimeUnit.SECONDS);
    }

    @Override
    public Map<Integer, AssetAvailabilityStatus> getAssetAvailabilityForUnknown(long messageId) {
        Set<Integer> deviceIdsUnkown = performanceVerificationDao.getDevicesWithUnknownStatus(messageId);
        return assetAvailabilityService.getAssetAvailabilityStatus(deviceIdsUnkown);
    }
    
    @Override
    public void processVerificationMessages(int deviceId, Map<Long, Instant> verificationMsgs, Range<Instant> range){
		/*
		 * 1. Remove invalid messages (message ids that were never sent to the
		 * device) 
		 * 2. Add messages received from unenrolled devices
		 * (SUCCESS_UNENROLLED) 
		 * 3. Mark received messages as success (SUCCESS)
		 * 4. Check if there was a broadcast event that happened between
		 * earliest relay start time and time of reading (UTC) and the result is
		 * still "UNKNOWN". Mark the result. (UNSUCCESS)
		 */
    	
    	//message ids that were sent to the device
		List<Long> sentMsgIds = performanceVerificationDao
				.getEventIds(new ArrayList<Long>(verificationMsgs.keySet()));
		//remove message ids that were never sent to the device
		Map<Long, Instant> successMsgs = Maps.filterKeys(verificationMsgs,
				Predicates.in(sentMsgIds));
    	
    	//SUCCESS_UNENROLLED
    	    	
    	List<Long> unexpectedMsgIds = new ArrayList<Long>(successMsgs.keySet());
    	// message ids we expect to receive from this device
		List<Long> expectedMsgIds = performanceVerificationDao.getEventIdsForDevice(
				deviceId, new ArrayList<Long>(successMsgs.keySet()));
		// Remove the message ids we expected to receive for this device. The
		// remaining message ids we didn't expect to get back because the device
		// was not enrolled in the program at the time the message was sent.
		unexpectedMsgIds.removeAll(expectedMsgIds);
		// keep track of successful responses for unenrolled devices
		for(Long eventId: unexpectedMsgIds){
			performanceVerificationDao.createUnenrolledEventResult(deviceId, eventId, successMsgs.get(eventId));
		}
		
		//SUCCESS
	
		// update only expected messages as a success
		Map<Long, Instant> successMsgsToMarkAsSuccess = Maps.filterKeys(successMsgs, Predicates.in(expectedMsgIds));
		if(!successMsgsToMarkAsSuccess.isEmpty()){
			performanceVerificationDao.updateSuccessEventResult(deviceId, successMsgsToMarkAsSuccess);
		}
		
		//UNSUCCESS
		
		if(range != null){
			performanceVerificationDao.updateUnsuccessEventResult(deviceId, range);
		}
    }
}