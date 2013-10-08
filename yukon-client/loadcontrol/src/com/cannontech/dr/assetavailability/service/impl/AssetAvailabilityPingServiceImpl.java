package com.cannontech.dr.assetavailability.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.AssetAvailabilityRelays;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.assetavailability.ping.AssetAvailabilityReadResult;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.google.common.base.Function;
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
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityPingServiceImpl.class);
    
    private Map<Integer, String> paoIdToResultIdMap = Maps.newConcurrentMap();
    
    public void readDevicesInDrGrouping(PaoIdentifier paoIdentifier, LiteYukonUser user) {
        //Get devices
        Set<YukonPao> devices = drGroupDeviceMappingDao.getDevicesForGrouping(paoIdentifier);
        
        //Get enrolled relays, so we know what attribute to read runtime on
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(paoIdentifier);
        Map<Integer, Integer> deviceIdToRelayMap = lmHardwareConfigurationDao.getDeviceIdToRelayMapByLoadGroups(loadGroupIds);
        
        Multimap<Integer, YukonPao> relayPaos = ArrayListMultimap.create();
        for(YukonPao device : devices) {
            Integer paoId = device.getPaoIdentifier().getPaoId();
            Integer relay = deviceIdToRelayMap.get(paoId);
            relayPaos.put(relay, device);
        }
        
        //Set up results callback
        AssetAvailabilityReadResult result = new AssetAvailabilityReadResult(Iterables.transform(devices, paoToIdFunction));
        String resultId = recentResultsCache.addResult(result);
        paoIdToResultIdMap.put(paoIdentifier.getPaoId(), resultId);
        
        //Read devices by relay
        log.info("Initiating asset availability read. PaoId = " + paoIdentifier.getPaoId() + ", Total Devices = " + devices.size());
        for(Map.Entry<Integer, Collection<YukonPao>> entry : relayPaos.asMap().entrySet()) {
            int relay = entry.getKey();
            Collection<YukonPao> paos = entry.getValue();
            if(AssetAvailabilityRelays.isValidRelay(relay)) {
                Attribute attribute = AssetAvailabilityRelays.getAttributeForRelay(relay);
                logRelayRead(paoIdentifier, paos.size(), relay);
                deviceAttributeReadService.initiateRead(paos, Sets.newHashSet(attribute), result, 
                                                        DeviceRequestType.ASSET_AVAILABILITY_READ, user);
            } else {
                DeviceAttributeReadError error = getBadRelayError(relay);
                for(YukonPao pao : paos) {
                    result.receivedError(pao.getPaoIdentifier(), error);
                }
                logRelayError(paoIdentifier, paos.size(), relay);
            }
        }
    }
    
    public AssetAvailabilityReadResult getReadResult(int paoId) {
        String resultId = paoIdToResultIdMap.get(paoId);
        if(resultId == null) {
            return null;
        }
        return recentResultsCache.getResult(resultId);
    }
    
    public boolean hasReadResult(int paoId) {
        String resultId = paoIdToResultIdMap.get(paoId);
        if(resultId == null) {
            return false;
        } else {
            AssetAvailabilityReadResult result = recentResultsCache.getResult(resultId);
            if(result == null || result.isComplete()) {
                paoIdToResultIdMap.remove(paoId); //remove old mapping
                return false;
            }
            return true;
        }
    }
    
    private DeviceAttributeReadError getBadRelayError(int relay) {
        MessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.web.modules.dr.assetAvailability.pingError.badRelay", relay);
        return new DeviceAttributeReadError(DeviceAttributeReadErrorType.NO_POINT, resolvable);
    }
    
    private static void logRelayRead(PaoIdentifier paoIdentifier, int size, int relay) {
        log.info("Asset availability read with paoId " + paoIdentifier.getPaoId() + " reading " + size + 
                 " devices on relay " + relay);
    }
    
    private static void logRelayError(PaoIdentifier paoIdentifier, int size, int relay) {
        log.error("Asset availability read with paoId " + paoIdentifier.getPaoId() + "skipped " + size + 
                  " reads on invalid relay " + relay);
    }
    
    //Function to pull paoId from a YukonPao
    private static final Function<YukonPao, Integer> paoToIdFunction = new Function<YukonPao, Integer>() {
        public Integer apply(YukonPao pao) {
            return pao.getPaoIdentifier().getPaoId();
        }
    };
}
