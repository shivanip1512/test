package com.cannontech.dr.rfn.service.impl;

import static com.cannontech.system.GlobalSettingType.RF_BROADCAST_PERFORMANCE;

import java.util.Collections;
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
import com.cannontech.dr.dao.PerformanceVerificationDao;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.impl.RfCommandStrategy;
import com.cannontech.system.PreferenceOnOff;
import com.cannontech.system.dao.GlobalSettingDao;

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
    public Map<Integer, AssetAvailabilityStatus> getAssetAvailabilityForUnknown(Range<Instant> dateRange,
                                                                                int numberPerPage,
                                                                                int pageNumber) {
        List<Integer> unknownDeviceIds = performanceVerificationDao.getDeviceIdsWithUnknownStatus(dateRange);
        int startIndex = (pageNumber-1) * numberPerPage;
        int endIndex = Math.min(startIndex+numberPerPage, unknownDeviceIds.size());

        if (startIndex > unknownDeviceIds.size()) {
            return Collections.emptyMap();
        }
        unknownDeviceIds = unknownDeviceIds.subList(startIndex, endIndex);

        return Collections.emptyMap();//assetAvailabilityService.getAssetAvailabilityStatus(unknownDeviceIds);
    }
}