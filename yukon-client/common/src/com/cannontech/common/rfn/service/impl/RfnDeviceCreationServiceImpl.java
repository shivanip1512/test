package com.cannontech.common.rfn.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.SimpleAlert;
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
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
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
    private static final String alertQueueName = JmsApiDirectory.RFN_DEVICE_CREATION_ALERT.getQueue().getName();
    private static final int incomingMessageWaitMillis = 1000;

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

    private String templatePrefix;
    private Cache<String, Boolean> recentlyUncreatableTemplates;
    private ConcurrentHashMultiset<String> unknownTemplatesEncountered = ConcurrentHashMultiset.create();
    private ConcurrentHashMultiset<RfnIdentifier> uncreatableDevices = ConcurrentHashMultiset.create();
    private Set<String> templatesToIgnore;
    private JmsTemplate jmsTemplate;
    
    private AtomicInteger deviceLookupAttempt = new AtomicInteger();
    private AtomicInteger newDeviceCreated = new AtomicInteger();

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
    }
    
    @Override
    @Transactional
    public RfnDevice create(final RfnIdentifier rfnIdentifier) {
        return createDevice(rfnIdentifier, null, null);
    }
    
    @Override
    @Transactional
    public RfnDevice create(final RfnIdentifier rfnIdentifier, Hardware hardware, LiteYukonUser user) {
        return createDevice(rfnIdentifier, hardware, user);
    }
    
    /**
     * Creates an rf devices, if the device is a dr device and create call is spawned from a NM archive request the 
     * hardware argument (and user) is expected to be null the stars tables will be stubbed out with defaults.  Otherwise 
     * callers of the {@link #create(final RfnIdentifier rfnIdentifier, Hardware hardware, LiteYukonUser user)} method are expected to
     * pass in a fully filled out (not null) hardware (and user).  This allows a single creation service for both types of 
     * users: rf message listeners LcrReadingArchiveRequestListener and 
     * stars operator controllers {@link OperatorHardwareController}, {@link AssetDashboardController}.
     */
    private RfnDevice createDevice(final RfnIdentifier rfnIdentifier, final Hardware hardware, final LiteYukonUser user) {
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
                        rfnDeviceEventLogService.receivedDataForUnkownDeviceTemplate(templateName);
                        log.warn("Unable to create device with template for " + rfnIdentifier, e);
                    }
                    throw e;
                } catch (DeviceCreationException e) {
                    int oldCount = uncreatableDevices.add(rfnIdentifier, 1);
                    if (oldCount == 0) {
                        // we may log this multiple times if the server is restarted, but this if statement
                        // seems to be a good idea to prevent excess 
                        rfnDeviceEventLogService.unableToCreateDeviceFromTemplate(templateName, rfnIdentifier.getSensorManufacturer(), rfnIdentifier.getSensorModel(), rfnIdentifier.getSensorSerialNumber());
                        log.warn("Unable to create device for " + rfnIdentifier, e);
                        
                            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.RFN_DEVICE_CREATION_FAILED");
                            resolvableTemplate.addData("rfnIdentifier", rfnIdentifier.toString());
                            resolvableTemplate.addData("errMessage", e.getMessage());
                            SimpleAlert simpleAlert = new SimpleAlert(AlertType.RFN_DEVICE_CREATION_FAILED, new Date(), resolvableTemplate);
                            jmsTemplate.convertAndSend(alertQueueName, simpleAlert);
                    }
                    throw e;
                } catch (EnergyCompanyNotFoundException e) {
                    int oldCount = uncreatableDevices.add(rfnIdentifier, 1);
                    if (oldCount == 0) {
                        // we may log this multiple times if the server is restarted, but this if statement
                        // seems to be a good idea to prevent excess 
                        rfnDeviceEventLogService.unableToCreateDeviceFromTemplate(templateName, rfnIdentifier.getSensorManufacturer(), rfnIdentifier.getSensorModel(), rfnIdentifier.getSensorSerialNumber());
                        log.warn("Unable to create device for " + rfnIdentifier, e);
                    }
                    throw e;
                }
            }

        });
        return result;
    }
    
    @Override
    public synchronized RfnDevice createGateway(String name, RfnIdentifier rfnIdentifier) {
        log.info("Creating gateway: " + rfnIdentifier);
        PaoType gatewayType;
        if (rfnIdentifier.getSensorModel().equalsIgnoreCase(GATEWAY_2_MODEL_STRING)) {
            gatewayType = PaoType.GWY800;
        } else {
            gatewayType = PaoType.RFN_GATEWAY;
        }
        
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
    
    @Override
    public void incrementDeviceLookupAttempt() {
        deviceLookupAttempt.incrementAndGet();
    }
    
    @Override
    public void incrementNewDeviceCreated() {
        newDeviceCreated.incrementAndGet();
    }
    
    @Override
    @ManagedAttribute
    public String getUnknownTemplates() {
        return unknownTemplatesEncountered.entrySet().toString();
    }
    
    @Override
    @ManagedAttribute
    public int getDeviceLookupAttempt() {
        return deviceLookupAttempt.get();
    }
    
    @Override
    @ManagedAttribute
    public int getNewDeviceCreated() {
        return newDeviceCreated.get();
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setReceiveTimeout(incomingMessageWaitMillis);
    }
    
}