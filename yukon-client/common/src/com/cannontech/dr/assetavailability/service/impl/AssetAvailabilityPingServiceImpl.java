package com.cannontech.dr.assetavailability.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.AssetAvailabilityRelays;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.assetavailability.ping.AssetAvailabilityReadResult;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class AssetAvailabilityPingServiceImpl implements AssetAvailabilityPingService {
    @Autowired @Qualifier("assetAvailabilityReads") private RecentResultsCache<AssetAvailabilityReadResult> recentResultsCache;
    @Autowired private DRGroupDeviceMappingDao drGroupDeviceMappingDao;
    @Autowired private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityPingServiceImpl.class);
    
    private final Map<Integer, String> paoIdToResultIdMap = Maps.newConcurrentMap();
    
    @Override
    public void readDevicesInDrGrouping(PaoIdentifier paoIdentifier, LiteYukonUser user) {
        Set<SimpleDevice> unavailableDevices = assetAvailabilityService.getUnavailableDevicesInDrGrouping(paoIdentifier);
        
        //Ensure we are not attempting to ping an unreasonable number of devices.
        if (unavailableDevices.size() > PING_MAXIMUM_DEVICES) {
            throw new IllegalArgumentException("No more than " + PING_MAXIMUM_DEVICES + " devices may be pinged. "
                                               + paoIdentifier.toString() + " contains " + unavailableDevices.size() + 
                                               " unavailable devices.");
        }
        
        //Get enrolled relays, so we know what attribute to read runtime on
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(paoIdentifier);
        Map<Integer, Integer> deviceIdToRelayMap = lmHardwareConfigurationDao.getDeviceIdToRelayMapByLoadGroups(loadGroupIds);
        
        Multimap<Integer, YukonPao> relayPaos = ArrayListMultimap.create();
        for(YukonPao device : unavailableDevices) {
            Integer paoId = device.getPaoIdentifier().getPaoId();
            Integer relay = deviceIdToRelayMap.get(paoId);
            relayPaos.put(relay, device);
        }
        
        //Set up results callback
        Iterable<Integer> paoIds = Iterables.transform(unavailableDevices, PaoUtils.getYukonPaoToPaoIdFunction());
        AssetAvailabilityReadResult result = new AssetAvailabilityReadResult(paoIds);
        String resultId = recentResultsCache.addResult(result);
        paoIdToResultIdMap.put(paoIdentifier.getPaoId(), resultId);
        
        //Read devices by relay
        log.info("Initiating asset availability read. PaoId = " + paoIdentifier.getPaoId() + ", Total Devices = " 
                + unavailableDevices.size());
        for(Map.Entry<Integer, Collection<YukonPao>> entry : relayPaos.asMap().entrySet()) {
            int relay = entry.getKey();
            Collection<YukonPao> paos = entry.getValue();
            if(AssetAvailabilityRelays.isValidRelay(relay)) {
                Attribute attribute = AssetAvailabilityRelays.getAttributeForRelay(relay);
                logRelayRead(paoIdentifier, paos.size(), relay);
                deviceAttributeReadService.initiateRead(paos, Sets.newHashSet(attribute), result, 
                                                        DeviceRequestType.ASSET_AVAILABILITY_READ, user);
            } else {
                SpecificDeviceErrorDescription error = getBadRelayError(relay);
                for(YukonPao pao : paos) {
                    result.receivedError(pao.getPaoIdentifier(), error);
                }
                logRelayError(paoIdentifier, paos.size(), relay);
            }
        }
    }
    
    @Override
    public AssetAvailabilityReadResult getReadResult(int paoId) {
        String resultId = paoIdToResultIdMap.get(paoId);
        if(resultId == null) {
            return null;
        }
        return recentResultsCache.getResult(resultId);
    }
    
    private SpecificDeviceErrorDescription getBadRelayError(int relay) {
        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.INVALID_ACTION);
        MessageSourceResolvable detail =
            new YukonMessageSourceResolvable("yukon.web.modules.dr.assetAvailability.pingError.badRelay", relay);
        return new SpecificDeviceErrorDescription(errorDescription, detail, detail);
    }

    private static void logRelayRead(PaoIdentifier paoIdentifier, int size, int relay) {
        log.info("Asset availability read with paoId " + paoIdentifier.getPaoId() + " reading " + size + 
                 " devices on relay " + relay);
    }
    
    private static void logRelayError(PaoIdentifier paoIdentifier, int size, int relay) {
        log.error("Asset availability read with paoId " + paoIdentifier.getPaoId() + "skipped " + size + 
                  " reads on invalid relay " + relay);
    }
}
