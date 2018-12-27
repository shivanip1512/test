package com.cannontech.dr.rfn.service.impl;

import static com.cannontech.system.GlobalSettingType.RF_BROADCAST_PERFORMANCE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.events.loggers.RfnPerformanceVerificationEventLogService;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Range;
import com.cannontech.dr.model.MutablePerformanceVerificationEventStats;
import com.cannontech.dr.model.PerformanceVerificationAverageReports;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.model.BroadcastEventDeviceDetails;
import com.cannontech.dr.rfn.model.DeviceStatus;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.impl.RfCommandStrategy;
import com.cannontech.system.OnOff;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RfnPerformanceVerificationServiceImpl implements RfnPerformanceVerificationService {
    private static final Logger log = YukonLogManager.getLogger(RfnPerformanceVerificationServiceImpl.class);
    
    @Autowired private RfCommandStrategy rfCommandStrategy;
    @Autowired private PerformanceVerificationDao performanceVerificationDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RfnPerformanceVerificationEventLogService performanceVerificationEventLog;
    
    @Override
    @Transactional
    public void sendPerformanceVerificationMessage() {
        OnOff preference = globalSettingDao.getEnum(RF_BROADCAST_PERFORMANCE, OnOff.class);
        
        if (preference == OnOff.ON) {
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
    public void processVerificationMessages(YukonPao device, Map<Long, Instant> verificationMsgs, Range<Instant> range){
        int deviceId = device.getPaoIdentifier().getPaoId();
		/*
		 * 1. Remove invalid messages
		 * 2. Add messages received from unenrolled devices
		 * (SUCCESS_UNENROLLED) 
		 * 3. Mark received messages as success (SUCCESS)
		 * 4. Check if there was a broadcast event that happened between
		 * earliest relay start time and time of reading (UTC) and the result is
		 * still "UNKNOWN". Mark the result. (FAILURE)
		 */
    	//message ids that were sent to the device
        List<Long> sentMsgIds = performanceVerificationDao.getValidEventIds(verificationMsgs.keySet());
		//remove message ids that were never sent to the device
		Map<Long, Instant> successMsgs = Maps.filterKeys(verificationMsgs,
				Predicates.in(sentMsgIds));
    	
    	//SUCCESS_UNENROLLED
    	    	
    	List<Long> unexpectedMsgIds = new ArrayList<Long>(successMsgs.keySet());
    	// message ids we expect to receive from this device
    	List<Long> expectedMsgIds = performanceVerificationDao.getValidEventIdsForDevice(deviceId, successMsgs.keySet());
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
		
		//FAILURE
		
		if(range != null){
			performanceVerificationDao.setEventResultStatusToFailure(deviceId, range);
		}
    }

    @Override
    public PerformanceVerificationAverageReports getAverageReports() {
        Instant now = new Instant();
        Range<Instant> lastDayRange = Range.inclusive(now.minus(Duration.standardDays(1)), now);
        // Not including most recent day, which is likely to be 'UNKNOWN' and will throw off stats
        Range<Instant> lastTwoDaysRange = 
                Range.inclusive(now.minus(Duration.standardDays(2)),now.minus(Duration.standardDays(1)));
        Range<Instant> lastSevenDaysRange = 
                Range.inclusive(now.minus(Duration.standardDays(7)), now.minus(Duration.standardDays(1)));
        Range<Instant> lastThirtyDaysRange = 
                Range.inclusive(now.minus(Duration.standardDays(30)), now.minus(Duration.standardDays(1)));

        Range<Instant> thirtyDaysRange = Range.inclusive(now.minus(Duration.standardDays(30)), now);
        List<PerformanceVerificationEventMessageStats> messageStats = 
                    performanceVerificationDao.getReports(thirtyDaysRange);

        MutablePerformanceVerificationEventStats oneDayStats = new MutablePerformanceVerificationEventStats();
        MutablePerformanceVerificationEventStats twoDaysStats = new MutablePerformanceVerificationEventStats();
        MutablePerformanceVerificationEventStats sevenDaysStats = new MutablePerformanceVerificationEventStats();
        MutablePerformanceVerificationEventStats thirtyDaysStats = new MutablePerformanceVerificationEventStats();

        for (PerformanceVerificationEventMessageStats messageStat : messageStats) {
            if (lastDayRange.intersects(messageStat.getTimeMessageSent())) {
                oneDayStats.addStats(messageStat.getNumSuccesses(),
                                     messageStat.getNumFailures(),
                                     messageStat.getNumUnknowns());
            } else {
                if (lastThirtyDaysRange.intersects(messageStat.getTimeMessageSent())) {
                    thirtyDaysStats.addStats(messageStat.getNumSuccesses(),
                                             messageStat.getNumFailures(),
                                             messageStat.getNumUnknowns());
                    if (lastSevenDaysRange.intersects(messageStat.getTimeMessageSent())) {
                        sevenDaysStats.addStats(messageStat.getNumSuccesses(),
                                                messageStat.getNumFailures(),
                                                messageStat.getNumUnknowns());
                        if (lastTwoDaysRange.intersects(messageStat.getTimeMessageSent())) {
                            twoDaysStats.addStats(messageStat.getNumSuccesses(),
                                                  messageStat.getNumFailures(),
                                                  messageStat.getNumUnknowns());
                        }
                    }
                }
            }
        }

        return new PerformanceVerificationAverageReports(oneDayStats.getImmutable(), twoDaysStats.getImmutable(),
                                                         sevenDaysStats.getImmutable(), thirtyDaysStats.getImmutable());
    }
    
    @Override
    public void archiveVerificationMessage() {
        DateTime removeBeforeDate = new DateTime().plusDays(-180).withTimeAtStartOfDay();
        performanceVerificationDao.archiveRfnBroadcastEventStatus(removeBeforeDate);
        performanceVerificationEventLog.archivedRfnBroadcastEventStatus(removeBeforeDate.minusDays(1));
    }
    
    @Autowired private PerformanceVerificationDao rfPerformanceDao;

    @Override
    public List<BroadcastEventDeviceDetails> getDevicesWithStatus(long eventId,
            PerformanceVerificationMessageStatus[] statuses, PagingParameters pagingParameters,
            List<DeviceGroup> subGroups) {
        ArrayList<PerformanceVerificationMessageStatus> status = Lists.newArrayList(statuses);
        boolean containsUnknown = status.contains(PerformanceVerificationMessageStatus.UNKNOWN);
        
        if (containsUnknown) {
            status.remove(PerformanceVerificationMessageStatus.UNKNOWN);
        }
        
        List<BroadcastEventDeviceDetails> deviceDetails = new ArrayList<>();
        deviceDetails = rfPerformanceDao.getFilteredDevicesWithStatus(eventId, status, pagingParameters, subGroups);
        
        if (containsUnknown) {
            List<BroadcastEventDeviceDetails> unknownDeviceDetails = new ArrayList<>();
            unknownDeviceDetails = rfPerformanceDao.getFilteredDevicesWithUnknownStatus(eventId, pagingParameters, subGroups);
            deviceDetails.addAll(unknownDeviceDetails);
        }
        return deviceDetails;
    }

    @Override
    public List<BroadcastEventDeviceDetails> getAllDevicesWithStatus(long eventId) {
        List<BroadcastEventDeviceDetails> details = new ArrayList<>();
        
        List<PerformanceVerificationMessageStatus> statuses = new ArrayList<>();
        statuses.add(PerformanceVerificationMessageStatus.SUCCESS);
        statuses.add(PerformanceVerificationMessageStatus.FAILURE);
        
        details = rfPerformanceDao.getAllDevicesWithStatus(eventId, statuses);
        details.addAll(rfPerformanceDao.getAllDevicesWithUnknownStatus(eventId));
        
        
        return details;
    }

    @Override
    public List<PaoIdentifier> getFilteredPaoForEvent(long eventId,
            PerformanceVerificationMessageStatus[] statuses, List<DeviceGroup> subGroups) {

        ArrayList<PerformanceVerificationMessageStatus> status = Lists.newArrayList(statuses);
        boolean containsUnknown = status.contains(PerformanceVerificationMessageStatus.UNKNOWN);
        
        if (containsUnknown) {
            status.remove(PerformanceVerificationMessageStatus.UNKNOWN);
        }
        
        List<PaoIdentifier> deviceDetails = new ArrayList<>();
        deviceDetails = rfPerformanceDao.getFilteredPaosWithStatus(eventId, status, subGroups);
        
        if (containsUnknown) {
            List<PaoIdentifier> unknownDeviceDetails = new ArrayList<>();
            unknownDeviceDetails = rfPerformanceDao.getFilteredPaoWithUnknownStatus(eventId, subGroups);
            deviceDetails.addAll(unknownDeviceDetails);
        }
        return deviceDetails;
    }
    
    @Override
    public PerformanceVerificationEventMessageStats getReportForEvent(long eventId) {
        return rfPerformanceDao.getReportForEvent(eventId);
    }
    
    @Override
    public Map<DeviceStatus, Integer> getUnknownCounts(long eventId) {
        return rfPerformanceDao.getUnknownCounts(eventId);
    }

    @Override
    public int getTotalCount(long eventId) {
        return rfPerformanceDao.getNumberOfDevices(eventId);
    }
}