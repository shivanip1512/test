package com.cannontech.services.pxmw.creation.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.dr.pxmw.model.PxMWVersion;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.simulators.message.request.PxMWDeviceAutoCreationSimulatonRequest;
import com.cannontech.simulators.message.request.PxMWSimulatorDeviceCreateRequest;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Strings;

public class PxMWDeviceCreationService {
    private static final Logger log = YukonLogManager.getLogger(PxMWDeviceCreationService.class);
    private static int runFrequencyHours = 24;

    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired PxMWCommunicationServiceV1 pxMWCommunicationServiceV1;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    private Set<HardwareType> supportedTypes = Set.of(HardwareType.LCR_6200C, HardwareType.LCR_6600C);
    private YukonJmsTemplate jmsTemplate;

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
        executor.scheduleAtFixedRate(autoCreateCloudLCRThread, 5, runFrequencyHours, TimeUnit.HOURS);
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.SIMULATORS);
        
    }
    
    public void simulateCreateCloudLCR(PxMWDeviceAutoCreationSimulatonRequest request) {
        autoCreateCloudLCR();
        //notify simulator that auto creation is done
        jmsTemplate.convertAndSend(new PxMWSimulatorDeviceCreateRequest(PxMWVersion.V1, true));
    }
    
    /**
     * Device Auto Creation runs on the interval specified in the settings with a default of 24 hours
     */
    private void autoCreateCloudLCR() {
        try {
            String siteGuid = getSiteGuid();
            if (Strings.isNullOrEmpty(siteGuid)) {
                return;
            }
            // if the auto creation is running, exit, otherwise set isRunning to "true" and continue
            if (!isRunning.compareAndSet(false, true)) {
                log.debug("Eaton Cloud LCR auto creation task is already running.");
                isRunning.set(false);
                return;
            }
            log.info("Eaton Cloud LCR auto creation started");

            // get list of Yukon devices (LCR6200C, 6600C) from DeviceGuid
            List<String> yukonGuids = deviceDao.getGuids();

            List<PxMWSiteDeviceV1> devicesToCreate = pxMWCommunicationServiceV1.getSiteDevices(siteGuid, null, true)
                    .getDevices();

            //remove device that exist in yukon
            devicesToCreate.removeIf(device -> yukonGuids.contains(device.getDeviceGuid()));
           
            if (devicesToCreate.isEmpty()) {
                log.info("Eaton Cloud LCR auto creation completed - no devices to create");
                isRunning.set(false);
                return;
            }
            
            
            List<PxMWTimeSeriesDeviceV1> timeSeriesDeviceRequest = devicesToCreate.stream()
                    .map(device -> new PxMWTimeSeriesDeviceV1(device.getDeviceGuid(), "110741"))
                    .collect(Collectors.toList());
            
            List<String> guidsWithTimeSeriesData = pxMWCommunicationServiceV1
                    .getTimeSeriesValues(timeSeriesDeviceRequest, getRange())
                    .stream()
                    .filter(deviceResult -> !CollectionUtils.isEmpty(deviceResult.getResults()))
                    .map(deviceID -> deviceID.getDeviceId())
                    .collect(Collectors.toList());
            
            //remove devices without time series data
            devicesToCreate.removeIf(device -> !guidsWithTimeSeriesData.contains(device.getDeviceGuid()));
    
            if (devicesToCreate.isEmpty()) {
                log.info("Eaton Cloud LCR auto creation completed - no devices to create");
                isRunning.set(false);
                return;
            }
            
            
            EnergyCompany ec = ecDao.getDefaultEnergyCompanyForThirdPartyApiOrSystemUsage();
            LiteStarsEnergyCompany lsec = starsDatabaseCache.getEnergyCompany(ec.getId());
            
            AtomicInteger createdDevices = new AtomicInteger();
            log.info("Eaton Cloud LCR auto creation starting, attempting to create {} devices", devicesToCreate.size());
            devicesToCreate.forEach(device -> {
                        try {                            
                            Hardware hardware = buildEatonCloudHardware(device);
                            List<YukonListEntry> typeEntries = yukonListDao.getYukonListEntry(hardware.getHardwareType().getDefinitionId(), lsec);
                            
                            if (typeEntries.isEmpty()) {
                                throw new DeviceCreationException("Missing Yukon List entry");
                            }
                            
                            YukonListEntry typeEntry = typeEntries.get(0);
                            hardware.setHardwareTypeEntryId(typeEntry.getEntryID());
                            if (supportedTypes.contains(hardware.getHardwareType())) {
                                hardwareUiService.createHardware(hardware, ec.getUser());
                                log.info("Created device for guid:{} name:{} type:{}", hardware.getGuid(),
                                        hardware.getDisplayName(),
                                        hardware.getHardwareType());
                                createdDevices.getAndIncrement();
                            } else {
                                log.info("Failed to create device for guid:{} name:{} type:{} - type is not supported",
                                        device.getDeviceGuid(),
                                        device.getName(), device.getModel());
                            }
                        } catch (Exception e) {
                            log.error("Failed to create device for guid:{} name:{} type:{}", device.getDeviceGuid(),
                                    device.getName(), device.getModel(), e);
                        }
                    });

            isRunning.set(false);
            log.info("Eaton Cloud LCR auto creation completed - {} new devices created", createdDevices.get());
        } catch (Exception e) {
            log.error("Unexpected exception: ", e);
            isRunning.set(false);
        }
    }

    //24 hour range
    private Range<Instant> getRange() {
        DateTime today = new DateTime(Instant.now());
        DateTime yesterday = today.minusDays(1);
        Range<Instant> timeRange = new Range<Instant>(yesterday.toInstant(), false, today.toInstant(), false);
        return timeRange;
    }

    private int getDeviceCreationInterval() {
        int deviceCreationInterval = settingDao.getInteger(GlobalSettingType.PX_MIDDLEWARE_DEVICE_CREATION_INTERVAL);
        return deviceCreationInterval;
    }

    private String getSiteGuid() {
        String siteGuid = settingDao.getString(GlobalSettingType.PX_MIDDLEWARE_SERVICE_ACCOUNT_ID);
        return siteGuid;
    }

    private Hardware buildEatonCloudHardware(PxMWSiteDeviceV1 device) {
        Hardware hardware = new Hardware();
        hardware.setGuid(device.getDeviceGuid());
        hardware.setDisplayName(device.getName());
        hardware.setHardwareType(HardwareType.valueOf(device.getModel()));
        hardware.setSerialNumber(device.getName());
        return hardware;
    }
}
