package com.cannontech.services.pxmw.creation.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.services.pxmw.creation.service.PxMWDeviceCreationService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class PxMWDeviceCreationServiceImpl implements PxMWDeviceCreationService{
    private static final Logger log = YukonLogManager.getLogger(PxMWDeviceCreationServiceImpl.class);
    private static int runFrequencyMinutes = 15;
    
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private HardwareUiService hardwareUiService;
    // I dont think this will work
//    @Autowired private YukonUserDao yukonUserDao;
    
    @Autowired PxMWCommunicationServiceV1 pxMWCommunicationServiceV1;

    private static AtomicBoolean isRunning = new AtomicBoolean();
    // Idk if I will need this
//    private MessageSourceAccessor systemMessageSourceAccessor;

    /**
     * The thread where the calculation is done.
     */
    private final Runnable autoCreateCloudLCRThread = this::autoCreateCloudLCR;
    
    /**
     * Schedule the calculation thread to run periodically.
     */
    @PostConstruct
    public void init() {
//        systemMessageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        runFrequencyMinutes = getDeviceCreationInterval();
        log.info("Auto creation of Eaton cloud LCRs will run every {} minutes and begin 5 minutes after startup", runFrequencyMinutes);
        executor.scheduleAtFixedRate(autoCreateCloudLCRThread, 5, runFrequencyMinutes, TimeUnit.MINUTES);
//        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.INFRASTRUCTURE_WARNINGS_CACHE_REFRESH);
    }
    
    /**
     * Device Auto Creation runs on the interval specified in the settings with a default of 24 hours
     */
    @Override
    public void autoCreateCloudLCR() {
        try {
            // if the auto creation is running, exit, otherwise set isRunning to "true" and continue
            if (!isRunning.compareAndSet(false, true)) {
                log.debug("Prevented start of Eaton Cloud LCR auto creation thread - task is already running.");
                return;
            }
            log.info("Beginning Eaton Cloud LCR auto creation");
            
            // get list of Yukon devices (LCR6200C, 6600C)
            List<String> yukonGUIDs = deviceDao.getGuids();
            
            // get devices from GetSiteDevice api, put in list
            List<String> siteDeviceGUIDs = pxMWCommunicationServiceV1.getSiteDevices(getSiteGuid(), null, null).getDevices()
                                           .stream()
                                           .map(siteDevice -> siteDevice.getDeviceUuid())
                                           .collect(Collectors.toList());;
            
            // Remove YukonGUIDs from siteDeviceGUIDs
            siteDeviceGUIDs.removeAll(yukonGUIDs);

            // Create object list using 11074 mFreq channel
            List<PxMWTimeSeriesDeviceV1> devicesToRequestDataFor = siteDeviceGUIDs.stream()
                                                                                  .map(p -> new PxMWTimeSeriesDeviceV1(p, "110741"))
                                                                                  .collect(Collectors.toList());

            // Calculate TimeStamps to get previous 1 day
            DateTime today = new DateTime(Instant.now());
            DateTime yesterday = today.minusDays(1);
            Range<Instant> timeRange = new Range<Instant>(yesterday.toInstant(), false, today.toInstant(), false);

            // Request data for devices
            PxMWTimeSeriesDataResponseV1 timeSeriesDataResponse = pxMWCommunicationServiceV1.getTimeSeriesValues(devicesToRequestDataFor, timeRange);
            
            // For everything with data, request device details required for creation
            // I don't think this is implemented yet
            
            // create yukon devices for all positive responses
            // Dont know if I can use this method or need to rip out some for just this service
            hardwareUiService.createHardware(new Hardware(), new LiteYukonUser());
            // log creation in the event logs
            
            isRunning.set(false);
        } catch (Exception e){
            log.error("Unexpected exception: ", e);
            isRunning.set(false);
        }
    }
    
    private int getDeviceCreationInterval() {
        int deviceCreationInterval = settingDao.getInteger(GlobalSettingType.PX_MIDDLEWARE_DEVICE_CREATION_INTERVAL);
        return deviceCreationInterval;
    }
    
    private String getSiteGuid() {
        String siteGuid = settingDao.getString(GlobalSettingType.PX_MIDDLEWARE_SERVICE_ACCOUNT_ID);
        return siteGuid;
    }
}
