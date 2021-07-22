package com.cannontech.common.rfn.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.SimpleAlert;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService.ChangeDeviceTypeInfo;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.RfnDeviceEventLogService;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.rfn.endpoint.IgnoredTemplateException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnModelChange;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.TransactionTemplateHelper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.ImmutableSet;

public class RfnDeviceCreationServiceImpl implements RfnDeviceCreationService {
    
    private static final Logger log = YukonLogManager.getLogger(RfnDeviceCreationServiceImpl.class);
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private RfnDeviceEventLogService rfnDeviceEventLogService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private HardwareUiService hardwareSevice;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private ChangeDeviceTypeService changeDeviceTypeService;

    private YukonJmsTemplate jmsTemplate;
    private String templatePrefix;
    private Cache<String, Boolean> recentlyUncreatableTemplates;
    private ConcurrentHashMultiset<String> unknownTemplatesEncountered = ConcurrentHashMultiset.create();
    private ConcurrentHashMultiset<RfnIdentifier> uncreatableDevices = ConcurrentHashMultiset.create();
    private Set<String> templatesToIgnore;

    @PostConstruct
    public void init() {
        ImmutableSet.Builder<String> ignoredTemplateBuilder = ImmutableSet.builder();
        String templatesToIgnoreConfigStr = configurationSource.getString("RFN_METER_TEMPLATES_TO_IGNORE", "");
        String[] ignoredTemplates = StringUtils.splitByWholeSeparator(templatesToIgnoreConfigStr, ",");
        for (String template : ignoredTemplates) {
            ignoredTemplateBuilder.add(template.trim());
        }
        templatesToIgnore = ignoredTemplateBuilder.build();
        
        recentlyUncreatableTemplates = CacheBuilder.newBuilder().concurrencyLevel(10).expireAfterWrite(5, TimeUnit.SECONDS).build();
    
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {
                    // no reason to be too delicate here
                    recentlyUncreatableTemplates.invalidateAll();
                }
            }
        });
        
        templatePrefix = configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, "*RfnTemplate_");
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RFN_DEVICE_CREATION_ALERT);
    }

    @Override
    @Transactional
    public RfnDevice createIfNotFound(RfnIdentifier newDeviceIdentifier, Instant dataTimestamp) {
        
        //use UTC date if there is no time stamp, DataTimestamp we get get from NM is in UTC format
        dataTimestamp = dataTimestamp == null ? new DateTime(DateTimeZone.UTC).toInstant() : dataTimestamp;
        
        if (newDeviceIdentifier == null || newDeviceIdentifier.is_Empty_()) {
            throw createRuntimeException("Unable to create or find device for " + newDeviceIdentifier);
        }
        try {
            return rfnDeviceDao.getDeviceForExactIdentifier(newDeviceIdentifier);
        } catch (NotFoundException e) {
            // find partially matching devices
            List<RfnDevice> devices = rfnDeviceDao.getPartiallyMatchedDevices(newDeviceIdentifier.getSensorSerialNumber(),
                    newDeviceIdentifier.getSensorManufacturer());

            // found multiple matching device, create exception
            if (devices.size() > 1) {
                throw createRuntimeException(
                        "Unable to create for " + newDeviceIdentifier + " found 2 or more partial matches " + devices);
            }

            // no partially matching devices, create devices
            if (devices.isEmpty()) {
                return create(newDeviceIdentifier);
            }

            RfnDevice partiallyMatchedDevice = devices.get(0);
            // partially matching device found, but it is not a meter, create new device
            if (!partiallyMatchedDevice.getPaoIdentifier().getPaoType().isMeter()) {
                return create(newDeviceIdentifier);
            }

            
            // time stamp of the last model change for this device
            Instant lastChangeDataTimestamp = rfnDeviceDao
                    .findModelChangeDataTimestamp(partiallyMatchedDevice.getPaoIdentifier().getPaoId());

            // the model was not updated ever, update the model
            if (lastChangeDataTimestamp == null) {
                return updateDeviceWithTheNewModel(newDeviceIdentifier, partiallyMatchedDevice, dataTimestamp);
            }
            
            // we probably made a model change recently, we found our device
            if (dataTimestamp.isBefore(lastChangeDataTimestamp) || lastChangeDataTimestamp.isEqual(dataTimestamp)) {
                return partiallyMatchedDevice;
            }
            
            //last change data stamp is older than this data's time stamp, update the model
            if (dataTimestamp.isAfter(lastChangeDataTimestamp)) {
                return updateDeviceWithTheNewModel(newDeviceIdentifier, partiallyMatchedDevice, dataTimestamp);
            }
            
            throw createRuntimeException("Unable to create or find device for " + newDeviceIdentifier);
        }
    }

    /**
     * Updates device with the new model and changes pao type is applicable
     */
    private RfnDevice updateDeviceWithTheNewModel(RfnIdentifier newDeviceIdentifier, RfnDevice partiallyMatchedDevice,
            Instant dataTimestamp) {
        String templateName = templatePrefix + newDeviceIdentifier.getSensorManufacturer() + "_" + newDeviceIdentifier.getSensorModel();
        SimpleDevice templateYukonDevice = deviceDao.getYukonDeviceObjectByName(templateName);
        RfnDevice updatedDevice = templateYukonDevice.getPaoIdentifier().getPaoType() != partiallyMatchedDevice.getPaoIdentifier()
                .getPaoType() ? changeDeviceType(newDeviceIdentifier, partiallyMatchedDevice,
                        templateYukonDevice) : updateRfnIdentifier(newDeviceIdentifier, partiallyMatchedDevice);
        RfnModelChange rfnModelChange = new RfnModelChange();
        rfnModelChange.setDataTimestamp(dataTimestamp);
        rfnModelChange.setDeviceId(partiallyMatchedDevice.getPaoIdentifier().getPaoId());
        rfnModelChange.setNewModel(newDeviceIdentifier.getSensorModel());
        rfnModelChange.setOldModel(partiallyMatchedDevice.getRfnIdentifier().getSensorModel());
        rfnDeviceDao.updateRfnModelChange(rfnModelChange);
        return updatedDevice;
    }

    private RuntimeException createRuntimeException(String error) {
        log.warn(error);
        return new RuntimeException(error);
    }
    
    private RuntimeException createRuntimeException(String error, Exception e) {
        log.warn(error, e);
        return new RuntimeException(error, e);
    }

    /**
     * Updating existing device with new rfn identifier, in this case a new model.
     */
    private RfnDevice updateRfnIdentifier(RfnIdentifier identifier, RfnDevice partiallyMatchedDevice) {
        RfnDevice updatedDevice = new RfnDevice(partiallyMatchedDevice.getName(),
                partiallyMatchedDevice.getPaoIdentifier(), identifier);
        rfnDeviceDao.updateDevice(updatedDevice);
        return rfnDeviceDao.getDevice(updatedDevice);
    }

    /**
     * Changing the device type and updating existing device with new rfn identifier, in this case a new model.
     */
    private RfnDevice changeDeviceType(RfnIdentifier identifier, RfnDevice partiallyMatchedDevice,
            SimpleDevice templateYukonDevice) {
        try {
            changeDeviceTypeService.changeDeviceType(new SimpleDevice(partiallyMatchedDevice),
                    templateYukonDevice.getPaoIdentifier().getPaoType(), new ChangeDeviceTypeInfo(identifier));
            return rfnDeviceDao.getDevice(partiallyMatchedDevice);
        } catch (Exception ex) {
            throw createRuntimeException("Unable to change device type for " + partiallyMatchedDevice + " from "
                    + partiallyMatchedDevice.getPaoIdentifier().getPaoType() + " to "
                    + templateYukonDevice.getPaoIdentifier().getPaoType());
        }
    }

    private RfnDevice create(RfnIdentifier identifier) {
        try {
            return create(identifier, null, null);
        } catch (IgnoredTemplateException e) {
            throw createRuntimeException("Unable to create device for " + identifier + " because template is ignored", e);
        } catch (BadTemplateDeviceCreationException e) {
            throw createRuntimeException(
                    "Creation failed for " + identifier + ". Manufacturer, Model and Serial Number combination do "
                            + "not match any templates.",
                    e);
        } catch (DeviceCreationException e) {
            log.warn("Creation failed for " + identifier + ", checking cache for any new entries.");
            // Try another lookup in case someone else beat us to it
            try {
                return rfnDeviceLookupService.getDevice(identifier);
            } catch (NotFoundException e1) {
                throw createRuntimeException("Creation failed for " + identifier, e);
            }
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                // Only log full exception when trace is on so lots of failed creations don't kill performance.
                throw createRuntimeException("Creation failed for " + identifier, e);
            } else {
                throw createRuntimeException("Creation failed for " + identifier +" : "+e);
            }
        }
    }
    
    /**
     * Creates an rf devices, if the device is a dr device and create call is spawned from a NM archive request the 
     * hardware argument (and user) is expected to be null the stars tables will be stubbed out with defaults.  Otherwise 
     * callers of the {@link #create(final RfnIdentifier rfnIdentifier, Hardware hardware, LiteYukonUser user)} method are expected to
     * pass in a fully filled out (not null) hardware (and user).  This allows a single creation service for both types of 
     * users: rf message listeners LcrReadingArchiveRequestListener and 
     * stars operator controllers {@link OperatorHardwareController}, {@link AssetDashboardController}.
     */
    @Override
    @Transactional
    public RfnDevice create(RfnIdentifier rfnIdentifier, Hardware hardware, LiteYukonUser user) {
        RfnDevice result = TransactionTemplateHelper.execute(transactionTemplate, new Callable<RfnDevice>() {

            @Override
            public RfnDevice call() {                
                String templateName = templatePrefix + rfnIdentifier.getSensorManufacturer() + "_" + rfnIdentifier.getSensorModel();
                if (templatesToIgnore.contains(templateName)) {
                    throw new IgnoredTemplateException();
                }
                
                if (recentlyUncreatableTemplates.asMap().containsKey(templateName)) {
                    // we already tried to create this template within the last few seconds and failed
                    unknownTemplatesEncountered.add(templateName, 1);
                    uncreatableDevices.add(rfnIdentifier);
                    throw new BadTemplateDeviceCreationException(templateName);
                }
                try {
                    String deviceName = rfnIdentifier.getSensorSerialNumber().trim();
                    YukonDevice newDevice = deviceCreationService.createDeviceByTemplate(templateName, deviceName, true);
                    RfnDevice device = new RfnDevice(deviceName, newDevice.getPaoIdentifier(), rfnIdentifier);
                    rfnDeviceDao.updateDevice(device);
                    if (newDevice.getPaoIdentifier().getPaoType().isMeter()) {
                        deviceDao.changeMeterNumber(device, deviceName);
                    }
                    
                    List<HardwareType> hardwareTypes = HardwareType.getForPaoType(newDevice.getPaoIdentifier().getPaoType());
                    
                    boolean isStars = !CollectionUtils.isEmpty(hardwareTypes) && hardwareTypes.size() == 1;
                    if (isStars) {
                        createStarsDevice(hardwareTypes.get(0), newDevice, rfnIdentifier, hardware, user);
                    }
                    
                    rfnDeviceEventLogService.createdNewDeviceAutomatically(device.getRfnIdentifier(), templateName, device.getName());
                    return device;
                } catch (BadTemplateDeviceCreationException e) {
                    recentlyUncreatableTemplates.put(templateName, Boolean.TRUE);
                    uncreatableDevices.add(rfnIdentifier, 1);
                    int oldCount = unknownTemplatesEncountered.add(templateName, 1);
                    if (oldCount == 0) {
                        // we may log this multiple times if the server is restarted, but this if statement
                        // seems to be a good idea to prevent excess 
                        rfnDeviceEventLogService.receivedDataForUnkownDeviceTemplate(templateName, rfnIdentifier.getSensorSerialNumber());
                        log.warn("Unable to create device with template for " + rfnIdentifier, e);
                    }
                    throw e;
                } catch (DeviceCreationException | EnergyCompanyNotFoundException e) {
                    int oldCount = uncreatableDevices.add(rfnIdentifier, 1);
                    if (oldCount == 0) {
                        // we may log this multiple times if the server is restarted, but this if statement
                        // seems to be a good idea to prevent excess 
                        rfnDeviceEventLogService.unableToCreateDeviceFromTemplate(templateName, rfnIdentifier.getSensorManufacturer(), rfnIdentifier.getSensorModel(), rfnIdentifier.getSensorSerialNumber());
                        log.warn("Unable to create device for " + rfnIdentifier, e);
                        
                        if (e instanceof DeviceCreationException) {
                            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.RFN_DEVICE_CREATION_FAILED");
                            resolvableTemplate.addData("rfnIdentifier", rfnIdentifier.toString());
                            resolvableTemplate.addData("errMessage", e.getMessage());
                            SimpleAlert simpleAlert = new SimpleAlert(AlertType.RFN_DEVICE_CREATION_FAILED, new Date(), resolvableTemplate);
                            jmsTemplate.convertAndSend(simpleAlert);
                        }
                    }
                    throw e;
                }
            }

        });
        log.debug("Created new device {}",  result);
        return result;
    }
    
    @Override
    public synchronized RfnDevice createGateway(String name, RfnIdentifier rfnIdentifier) {
        log.info("Creating gateway: " + rfnIdentifier);
        Map<String, PaoType> modelTypes = Map.of(
                GATEWAY_2_MODEL_STRING.toLowerCase(), PaoType.GWY800,
                GATEWAY_3_MODEL_STRING.toLowerCase(), PaoType.VIRTUAL_GATEWAY,
                GATEWAY_4_MODEL_STRING.toLowerCase(), PaoType.GWY801);

        PaoType gatewayType = modelTypes.getOrDefault(rfnIdentifier.getSensorModel().toLowerCase(),
                PaoType.RFN_GATEWAY);
        SimpleDevice device = deviceCreationService.createRfnDeviceByDeviceType(gatewayType, name, rfnIdentifier, true);
        return new RfnDevice(name, device.getPaoIdentifier(), rfnIdentifier);
    }

    private void createStarsDevice(HardwareType type, YukonDevice device, RfnIdentifier rfnIdentifier, Hardware hardware, LiteYukonUser user) {
        if (hardware == null) {
            /** Attempt to stub out a stars devices for lcr archive messages */ 
            EnergyCompany ec = ecDao.getDefaultEnergyCompanyForThirdPartyApiOrSystemUsage();
            LiteStarsEnergyCompany lsec = starsDatabaseCache.getEnergyCompany(ec.getId());
            
            List<YukonListEntry> typeEntries = yukonListDao.getYukonListEntry(type.getDefinitionId(), lsec);
            
            if (typeEntries.isEmpty()) {
                throw new DeviceCreationException("Energy company " + ec.getName() + " has no device for type: " + device.getPaoIdentifier().getPaoType(),"invalidDevice", ec.getName() ,device.getPaoIdentifier().getPaoType());
            }
            
            hardware = new Hardware();
            YukonListEntry typeEntry = typeEntries.get(0);
            hardware.setHardwareTypeEntryId(typeEntry.getEntryID());
            hardware.setHardwareType(HardwareType.valueOf(typeEntry.getYukonDefID()));
            hardware.setSerialNumber(rfnIdentifier.getSensorSerialNumber());
            hardware.setDeviceId(device.getPaoIdentifier().getPaoId());
            hardware.setEnergyCompanyId(ec.getId());
            
            List<YukonListEntry> statusTypeEntries = yukonListDao.getYukonListEntry(YukonDefinition.DEV_STAT_INSTALLED.getDefinitionId(), lsec);
            hardware.setDeviceStatusEntryId(statusTypeEntries.get(0).getEntryID());
            user = lsec.getUser();
        }
        
        hardware.setDeviceId(device.getPaoIdentifier().getPaoId());
        hardwareSevice.createHardware(hardware, user);
    }
}