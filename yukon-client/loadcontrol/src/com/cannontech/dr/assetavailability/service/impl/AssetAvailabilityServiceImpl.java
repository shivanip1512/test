package com.cannontech.dr.assetavailability.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.dr.assetavailability.ApplianceRuntime;
import com.cannontech.dr.assetavailability.ApplianceWithRuntime;
import com.cannontech.dr.assetavailability.AssetAvailability;
import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class AssetAvailabilityServiceImpl implements AssetAvailabilityService {
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    @Autowired private ApplianceDao applianceDao;
    @Autowired private InventoryDao inventoryDao;
    
    private static final int LAST_COMMUNICATION_HOURS = 60;
    private static final int LAST_RUNTIME_HOURS = 168;
    private static final Map<Integer, Attribute> RELAY_ATTRIBUTES = Maps.newHashMap();
    
    static {
        RELAY_ATTRIBUTES.put(1, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG);
        RELAY_ATTRIBUTES.put(2, BuiltInAttribute.RELAY_2_RUN_TIME_DATA_LOG);
        RELAY_ATTRIBUTES.put(3, BuiltInAttribute.RELAY_3_RUN_TIME_DATA_LOG);
        RELAY_ATTRIBUTES.put(4, BuiltInAttribute.RELAY_4_RUN_TIME_DATA_LOG);
    }
    
    @Override
    public AssetAvailability getAssetAvailability(int deviceId) throws NoInventoryException {
        InventoryIdentifier inventoryIdentifier;
        int inventoryId;
        try {
            inventoryIdentifier = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        } catch(IncorrectResultSizeDataAccessException e) {
            throw new NoInventoryException("Cannot retrieve asset availability: invalid device ID: "
                                           + deviceId, e);
        }
        inventoryId = inventoryIdentifier.getInventoryId();
        
        //determine if opted out
        CustomerAccount account = customerAccountDao.getAccountByInventoryId(inventoryId);
        boolean isOptedOut = optOutEventDao.isOptedOut(inventoryId, account.getAccountId());
        
        //get last communication time
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        Range<Instant> lastCommRange = Range.unbounded();
        Instant lastCommunicationTime = rawPointHistoryDao.getLastDataDateInRange(device.getPaoIdentifier(), lastCommRange);
        
        //get connected appliances
        List<ApplianceWithRuntime> appliancesWithRuntime = Lists.newArrayList();
        List<LMHardwareConfiguration> configs = lmHardwareConfigurationDao.getForInventoryId(inventoryId);
        for(LMHardwareConfiguration config : configs) {
            Appliance appliance = applianceDao.getById(config.getApplianceId());
            int relay = config.getLoadNumber();
            Attribute attribute = RELAY_ATTRIBUTES.get(relay);
            //get most recent non-zero runtime
            //if runtime was never reported, value is null
            PointValueQualityHolder pvqh = rawPointHistoryDao.getLastNonZeroAttributeData(device, attribute);
            
            ApplianceRuntime runtime;
            if(pvqh != null) {
                runtime = new ApplianceRuntime(pvqh.getPointDataTimeStamp(), pvqh.getValue());
            } else {
                runtime = ApplianceRuntime.NONE;
            }
            appliancesWithRuntime.add(new ApplianceWithRuntime(appliance, runtime));
        }
        
        AssetAvailabilityStatus assetAvailability = calculateAssetAvailabilityStatus(lastCommunicationTime, appliancesWithRuntime);
        
        return new AssetAvailability(deviceId, assetAvailability, isOptedOut, lastCommunicationTime, appliancesWithRuntime);
    }

    @Override
    public Set<AssetAvailability> getAssetAvailability(Collection<Integer> deviceIds) {
        Set<AssetAvailability> assetAvailabilities = Sets.newHashSet();
        for(Integer deviceId : deviceIds) {
            AssetAvailability assetAvailability;
            try {
                assetAvailability = getAssetAvailability(deviceId);
            } catch(NoInventoryException e) {
                assetAvailability = AssetAvailability.getEmptyAvailability(deviceId);
            }
            
            assetAvailabilities.add(assetAvailability);
        }
        return assetAvailabilities;
    }
    
    private AssetAvailabilityStatus calculateAssetAvailabilityStatus(Instant lastCommunicationTime,
            List<ApplianceWithRuntime> appliancesWithRuntime) {
        Instant endOfCommsWindow= Instant.now().minus(Duration.standardHours(LAST_COMMUNICATION_HOURS));
        Instant endOfRuntimeWindow = Instant.now().minus(Duration.standardHours(LAST_RUNTIME_HOURS));
        
        boolean hasCommunicated = lastCommunicationTime != null && lastCommunicationTime.isAfter(endOfCommsWindow);
        
        boolean hasNonZeroRuntime = false;
        for(ApplianceWithRuntime applianceWithRuntime : appliancesWithRuntime) {
            Instant runtimeReadDate = applianceWithRuntime.getRuntime().getReadDate();
            if(runtimeReadDate != null && runtimeReadDate.isAfter(endOfRuntimeWindow)) {
                hasNonZeroRuntime = true;
                break;
            }
        }
        
        if(hasCommunicated) {
            if(hasNonZeroRuntime) return AssetAvailabilityStatus.IN_COMMUNICATION_RUNNING;
            return AssetAvailabilityStatus.IN_COMMUNICATION_NOT_RUNNING;
        }
        
        return AssetAvailabilityStatus.UNAVAILABLE;
    }
}
