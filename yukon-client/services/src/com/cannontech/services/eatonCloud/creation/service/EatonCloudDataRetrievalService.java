package com.cannontech.services.eatonCloud.creation.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
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
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.dr.eatonCloud.model.EatonCloudChannel;
import com.cannontech.dr.eatonCloud.model.EatonCloudVersion;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteDeviceV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDeviceV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudDataReadService;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.simulators.message.request.EatonCloudDataRetrievalSimulatonRequest;
import com.cannontech.simulators.message.request.EatonCloudSimulatorDeviceCreateRequest;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class EatonCloudDataRetrievalService {
    private static final Logger log = YukonLogManager.getLogger(EatonCloudDataRetrievalService.class);

    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired EatonCloudCommunicationServiceV1 eatonCloudCommunicationServiceV1;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private EatonCloudDataReadService eatonCloudDataReadService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationSource configurationSource;
    private YukonJmsTemplate jmsTemplate;

    private static AtomicBoolean isRunningDeviceCreation = new AtomicBoolean();
    private static AtomicBoolean isRunningDeviceRead = new AtomicBoolean();
    
    private ScheduledFuture<?> scheduledFutureAutoCreate;
    private ScheduledFuture<?> scheduledFutureDeviceRead;

    /**
     * Schedule the calculation and data read threads to run periodically.
     */
    @PostConstruct
    public void init() {
        String siteGuid = getSiteGuid();
        if (Strings.isNullOrEmpty(siteGuid)) {
            return;
        }
        
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.SIMULATORS);
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING, this::databaseChangeEvent);
       // scheduleDeviceAutoCreate(5);
      //  scheduleDeviceRead(5);
    }


    private void scheduleDeviceAutoCreate(long initialDelay) {
        // hours
        int creationInterval = settingDao.getInteger(GlobalSettingType.EATON_CLOUD_DEVICE_CREATION_INTERVAL);
        log.info("Auto creation of Eaton cloud LCRs will run every {} hours", creationInterval);
        scheduledFutureAutoCreate = executor.scheduleAtFixedRate(() -> autoCreateCloudLCRs(creationInterval), initialDelay,
                creationInterval * 60,
                TimeUnit.MINUTES);
    }

    private void scheduleDeviceRead(long initialDelay) {
        // minutes
        int readInterval = settingDao.getInteger(GlobalSettingType.EATON_CLOUD_DEVICE_READ_INTERVAL_MINUTES);
        log.info("Auto read of Eaton cloud LCRs will run every {} minutes", readInterval);
        scheduledFutureDeviceRead = executor.scheduleAtFixedRate(() -> readCloudLCRs(readInterval), initialDelay, readInterval,
                TimeUnit.MINUTES);
    }

    /**
     * Called when any global setting is updated
     */
    private void databaseChangeEvent(DatabaseChangeEvent event) {
        if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.EATON_CLOUD_DEVICE_CREATION_INTERVAL)) {
            if (scheduledFutureAutoCreate != null) {
                scheduledFutureAutoCreate.cancel(true);
            }
            scheduleDeviceAutoCreate(0);
        }
        if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.EATON_CLOUD_DEVICE_READ_INTERVAL_MINUTES)) {
            if (scheduledFutureDeviceRead != null) {
                scheduledFutureDeviceRead.cancel(true);
            }
            scheduleDeviceRead(0);
        }
    }

    public void startSimulation(EatonCloudDataRetrievalSimulatonRequest request) {
        if (request.isCreate()) {
            int creationInterval = settingDao.getInteger(GlobalSettingType.EATON_CLOUD_DEVICE_CREATION_INTERVAL);
            autoCreateCloudLCRs(creationInterval);
            // notify simulator that auto creation is done
            jmsTemplate.convertAndSend(new EatonCloudSimulatorDeviceCreateRequest(EatonCloudVersion.V1, true));
        } else {
            int readInterval = settingDao.getInteger(GlobalSettingType.EATON_CLOUD_DEVICE_READ_INTERVAL_MINUTES);
            readCloudLCRs(readInterval);
        }
    }

    private void readCloudLCRs(int readInterval) {
        if (isRunningDeviceRead.get() == true) {
            log.debug("Eaton Cloud LCR read all task is already running.");
            return;
        }
        isRunningDeviceRead.set(true);
        log.info("Eaton Cloud read all LCRs task started. [INTERVAL {} MINUTES]", readInterval);
        List<Integer> deviceIds = deviceDao.getDeviceIdsWithGuids();
        try {
            Multimap<PaoIdentifier, PointData> data = eatonCloudDataReadService.collectDataForRead(new HashSet<>(deviceIds),
                    getIntervalReadRange(), "READ ON INTERVAL-"+ readInterval);
            log.info("Eaton Cloud read [INTERVAL {} MINUTES] all LCRs task completed, devices attempted read:{} Read succeeded for {} devices.", readInterval, deviceIds.size(),
                    data.asMap().keySet().size());
        } catch (Exception e) {
            log.info("Eaton Cloud read LCRs task failed [INTERVAL {} MINUTES]", readInterval, e);
        }
        isRunningDeviceRead.set(false);
    }
    
    /**
     * Device Auto Creation runs on the interval specified in the settings with a default of 24 hours
     * @param creationInterval 
     */
    private void autoCreateCloudLCRs(int creationInterval) {
        try {
            // if the auto creation is running, exit, otherwise set isRunning to "true" and continue
            if (isRunningDeviceCreation.get() == true) {
                log.debug("Eaton Cloud LCR auto creation task is already running.");
                return;
            }
            isRunningDeviceCreation.set(true);
            log.info("Eaton Cloud LCR auto creation started. [INTERVAL {} HOURS]", creationInterval);

            // get list of Yukon devices (LCR6200C, 6600C) from DeviceGuid
            List<String> yukonGuids = deviceDao.getGuids();

            List<EatonCloudSiteV1> sites = eatonCloudCommunicationServiceV1.getSites(getSiteGuid());
            
            List<EatonCloudSiteDeviceV1> devicesToCreate = 
                    sites.stream()
                        .map(EatonCloudSiteV1::getSiteGuid)
                        .map(guid -> eatonCloudCommunicationServiceV1.getSiteDevices(guid, null, true))
                        .flatMap(siteDevices -> siteDevices.getDevices().stream())
                        .collect(Collectors.toList());
            
            //remove device that exist in yukon
            devicesToCreate.removeIf(device -> yukonGuids.contains(device.getDeviceGuid()));
           
            if (devicesToCreate.isEmpty()) {
                log.info("Eaton Cloud LCR auto creation completed - no devices to create.  [INTERVAL {} HOURS]", creationInterval);
                isRunningDeviceCreation.set(false);
                return;
            }
            
            int channelCount = configurationSource.getBoolean(MasterConfigBoolean.EATON_CLOUD_JOBS_TREND, false) ? 1000 : 10;
            
            List<List<EatonCloudTimeSeriesDeviceV1>> timeSeriesDeviceRequests = Lists.partition(devicesToCreate.stream()
                    .map(device -> new EatonCloudTimeSeriesDeviceV1(device.getDeviceGuid(), String.valueOf(EatonCloudChannel.FREQUENCY.getChannelId())))
                    .collect(Collectors.toList()), channelCount);
            
            List<String> guidsWithTimeSeriesData = new ArrayList<>();
            
            timeSeriesDeviceRequests.forEach(timeSeriesDeviceRequest -> {
                guidsWithTimeSeriesData.addAll(eatonCloudCommunicationServiceV1
                        .getTimeSeriesValues(timeSeriesDeviceRequest, getRange())
                        .stream()
                        .filter(deviceResult -> !CollectionUtils.isEmpty(deviceResult.getResults()))
                        .map(deviceID -> deviceID.getDeviceId())
                        .collect(Collectors.toList()));
            });
            
            //remove devices without time series data
            devicesToCreate.removeIf(device -> !guidsWithTimeSeriesData.contains(device.getDeviceGuid()));
    
            if (devicesToCreate.isEmpty()) {
                log.info("Eaton Cloud LCR auto creation completed - no devices to create.  [INTERVAL {} HOURS]", creationInterval);
                isRunningDeviceCreation.set(false);
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
                            if (hardware.getHardwareType().isEatonCloud()) {
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

            isRunningDeviceCreation.set(false);
            log.info("Eaton Cloud LCR auto creation completed - {} new devices created. [INTERVAL {} HOURS]", createdDevices.get(), creationInterval);
        } catch (Exception e) {
            log.error("Unexpected exception: ", e);
            isRunningDeviceCreation.set(false);
        }
    }

    //24 hour range
    private Range<Instant> getRange() {
        DateTime today = new DateTime(Instant.now());
        DateTime yesterday = today.minusDays(1);
        Range<Instant> timeRange = new Range<Instant>(yesterday.toInstant(), false, today.toInstant(), false);
        return timeRange;
    }

    /**
     * Range is interval plus up to 59Min
     * Rounds to hourly FLOOR(NOW-INTERVAL)
     * Hourly read at 1:59 has range 12:00 - 1:59
     * Hourly read at 1:01 has range 12:00-1:01
     */
    private Range<Instant> getIntervalReadRange() {
        var now = DateTime.now();
        int readInterval = settingDao.getInteger(GlobalSettingType.EATON_CLOUD_DEVICE_READ_INTERVAL_MINUTES);
        DateTime startTime = now.minusMinutes(readInterval);
        startTime = startTime.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        return new Range<Instant>(startTime.toInstant(), false, now.toInstant(), false);
    }

    private String getSiteGuid() {
        return settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
    }

    private Hardware buildEatonCloudHardware(EatonCloudSiteDeviceV1 device) {
        Hardware hardware = new Hardware();
        hardware.setGuid(device.getDeviceGuid());
        hardware.setDisplayName(device.getName());
        hardware.setHardwareType(HardwareType.valueOf(device.getModel()));
        hardware.setSerialNumber(device.getSerial());
        return hardware;
    }
}
