package com.cannontech.dr.rfn.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.Range;
import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

public class RfnPerformanceVerificationServiceImpl implements RfnPerformanceVerificationService {
    @Autowired private PerformanceVerificationDao performanceVerificationDao;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    
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