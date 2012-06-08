package com.cannontech.common.rfn.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.events.loggers.RfnDeviceEventLogService;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.rfn.endpoint.IgnoredTemplateException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnArchiveRequestService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.TransactionTemplateHelper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.ImmutableSet;

public class RfnArchiveRequestServiceImpl implements RfnArchiveRequestService{
    private static final Logger log = YukonLogManager.getLogger(RfnArchiveRequestServiceImpl.class);

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private RfnDeviceEventLogService rfnDeviceEventLogService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private HardwareUiService hardwareSevice;

    private String templatePrefix;
    private Cache<String, Boolean> recentlyUncreatableTemplates;
    private ConcurrentHashMultiset<String> unknownTemplatesEncountered = ConcurrentHashMultiset.create();
    private ConcurrentHashMultiset<RfnIdentifier> uncreatableDevices = ConcurrentHashMultiset.create();
    private Set<String> templatesToIgnore;
    private int workerCount;
    private int queueSize;
    private AtomicInteger deviceLookupAttempt = new AtomicInteger();
    private AtomicInteger newDeviceCreated = new AtomicInteger();
    private AtomicInteger processedArchiveRequest = new AtomicInteger();

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
        
        workerCount = configurationSource.getInteger("RFN_METER_DATA_WORKER_COUNT", 5);
        queueSize = configurationSource.getInteger("RFN_METER_DATA_WORKER_QUEUE_SIZE", 500);
        
        templatePrefix = configurationSource.getString("RFN_METER_TEMPLATE_PREFIX", "*RfnTemplate_");
    }
    
    @Transactional
    public RfnDevice createDevice(final RfnIdentifier rfnIdentifier) {
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
                    RfnDevice device = new RfnDevice(newDevice.getPaoIdentifier(), rfnIdentifier);
                    rfnDeviceDao.updateDevice(device);
                    if (newDevice.getPaoIdentifier().getPaoType().isMeter()) {
                        deviceDao.changeMeterNumber(device, deviceName);
                    }
                    
                    List<HardwareType> hardwareTypes = HardwareType.getForPaoType(newDevice.getPaoIdentifier().getPaoType());
                    
                    if (!CollectionUtils.isEmpty(hardwareTypes) && hardwareTypes.size() == 1) {
                        String ecName = configurationSource.getString("RFN_ENERGY_COMPANY_NAME");
                        if (StringUtils.isEmpty(ecName)) {
                            throw new DeviceCreationException("RF Yukon systems with DR devices are required to specify the RFN_ENERGY_COMPANY_NAME configuration property in master.cfg");
                        }
                        final LiteEnergyCompany ec = energyCompanyDao.getEnergyCompanyByName(ecName);
                        LiteStarsEnergyCompany lsec = starsDatabaseCache.getEnergyCompany(ec.getEnergyCompanyID());
                        
                        HardwareType ht = hardwareTypes.get(0);
                        
                        List<YukonListEntry> typeEntries = yukonListDao.getYukonListEntry(ht.getDefinitionId(), lsec);
                        
                        if (typeEntries.isEmpty()) throw new DeviceCreationException("Energy company " + ecName + " has no device for type: " + newDevice.getPaoIdentifier().getPaoType());
                        
                        Hardware h = new Hardware();
                        h.setHardwareTypeEntryId(typeEntries.get(0).getEntryID());
                        h.setSerialNumber(rfnIdentifier.getSensorSerialNumber());
                        h.setDeviceId(newDevice.getPaoIdentifier().getPaoId());
                        h.setEnergyCompanyId(ec.getEnergyCompanyID());
                        
                        List<YukonListEntry> statusTypeEntries = yukonListDao.getYukonListEntry(YukonDefinition.DEV_STAT_INSTALLED.getDefinitionId(), lsec);
                        h.setDeviceStatusEntryId(statusTypeEntries.get(0).getEntryID());
                        
                        hardwareSevice.createHardware(h, 0, lsec.getUser());
                    }
                    
                    rfnDeviceEventLogService.createdNewDeviceAutomatically(device.getPaoIdentifier().getPaoId(), device.getRfnIdentifier().getCombinedIdentifier(), templateName, deviceName);
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
                    }
                    throw e;
                }
            }

        });
        return result;
    }
    
    public void incrementProcessedArchiveRequest() {
        processedArchiveRequest.incrementAndGet();
    }
    
    public void incrementDeviceLookupAttempt() {
        deviceLookupAttempt.incrementAndGet();
    }
    
    public void incrementNewDeviceCreated() {
        newDeviceCreated.incrementAndGet();
    }
    
    @ManagedAttribute
    public int getWorkerCount() {
        return workerCount;
    }
    
    @ManagedAttribute
    public int getQueueSize() {
        return queueSize;
    }
    
    @ManagedAttribute
    public String getUnkownTempaltes() {
        return unknownTemplatesEncountered.entrySet().toString();
    }
    
    @ManagedAttribute
    public int getDeviceLookupAttempt() {
        return deviceLookupAttempt.get();
    }
    
    @ManagedAttribute
    public int getNewDeviceCreated() {
        return newDeviceCreated.get();
    }
    
    @ManagedAttribute
    public int getProcessedArchiveRequest() {
        return processedArchiveRequest.get();
    }
    
}