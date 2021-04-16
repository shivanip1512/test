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
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.services.pxmw.creation.service.PxMWDeviceCreationService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;

public class PxMWDeviceCreationServiceImpl implements PxMWDeviceCreationService {
    private static final Logger log = YukonLogManager.getLogger(PxMWDeviceCreationServiceImpl.class);
    private static int runFrequencyHours = 24;

    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired PxMWCommunicationServiceV1 pxMWCommunicationServiceV1;

    private static AtomicBoolean isRunning = new AtomicBoolean();

    /**
     * The thread where the calculation is done.
     */
    private final Runnable autoCreateCloudLCRThread = this::autoCreateCloudLCR;

    /**
     * Schedule the calculation thread to run periodically.
     */
    @PostConstruct
    public void init() {
        runFrequencyHours = getDeviceCreationInterval();
        log.info("Auto creation of Eaton cloud LCRs will run every {} hours and begin 5 minutes after startup",
                runFrequencyHours);
        executor.scheduleAtFixedRate(autoCreateCloudLCRThread, 5, runFrequencyHours, TimeUnit.MINUTES);
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

            // get list of Yukon devices (LCR6200C, 6600C) from DeviceGuid
            List<String> yukonGUIDs = deviceDao.getGuids();

            // get siteDevices with details
            List<PxMWSiteDeviceV1> siteDevicesWithDetails = pxMWCommunicationServiceV1.getSiteDevices(getSiteGuid(), null, true)
                    .getDevices();

            // build list of siteDeviceGUIDs and remove YukonGUIDs from siteDeviceGUIDs
            List<String> siteDeviceGUIDs = siteDevicesWithDetails.stream()
                    .map(siteDevice -> siteDevice.getDeviceGuid())
                    .filter(siteDeviceGuid -> yukonGUIDs.contains(siteDeviceGuid))
                    .collect(Collectors.toList());

            // Create object list using 110741 mFreq channel
            List<PxMWTimeSeriesDeviceV1> devicesToRequestDataFor = siteDeviceGUIDs.stream()
                    .map(p -> new PxMWTimeSeriesDeviceV1(p, "110741"))
                    .collect(Collectors.toList());

            // Calculate TimeStamps to get previous 1 day
            DateTime today = new DateTime(Instant.now());
            DateTime yesterday = today.minusDays(1);
            Range<Instant> timeRange = new Range<Instant>(yesterday.toInstant(), false, today.toInstant(), false);

            // Request data for devices
            List<String> timeSeriesDataResponseDeviceIDs = pxMWCommunicationServiceV1
                    .getTimeSeriesValues(devicesToRequestDataFor, timeRange)
                    .stream()
                    .filter(deviceResult -> deviceResult.getResults() != null)
                    .map(deviceID -> deviceID.getDeviceId())
                    .collect(Collectors.toList());

            // Filter out devices that won't be created
            List<PxMWSiteDeviceV1> siteDevicesToCreate = siteDevicesWithDetails.stream()
                    .filter(detailDevices -> timeSeriesDataResponseDeviceIDs.contains(detailDevices.getDeviceGuid()))
                    .collect(Collectors.toList());

            // Get Yukon User
            LiteYukonUser yukonUser = YukonUserContext.system.getYukonUser();
            // Create Yukon devices for all positive responses
            siteDevicesToCreate.stream()
                    .map(device -> buildEatonCloudHardware(device.getDeviceGuid(), device.getName(), device.getModel()))
                    .map(hardware -> hardwareUiService.createHardware(hardware, yukonUser));

            isRunning.set(false);
            log.info("Eaton Cloud LCR auto creation completed, {} new devices created", siteDevicesToCreate.size());
        } catch (Exception e) {
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

    private Hardware buildEatonCloudHardware(String guid, String displayName, String model) {
        Hardware hardware = new Hardware();
        hardware.setGuid(guid);
        hardware.setDisplayName(displayName);
        hardware.setHardwareType(HardwareType.valueOf(model));

        return hardware;
    }
}
