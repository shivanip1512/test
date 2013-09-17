package com.cannontech.dr.assetavailability.service.impl;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.dr.assetavailability.ping.AssetAvailabilityReadResult;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityRfnPingService;
import com.google.common.collect.Sets;

public class AssetAvailabilityRfnPingServiceImpl implements AssetAvailabilityRfnPingService {
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityRfnPingServiceImpl.class);
    @Autowired @Qualifier("assetAvailabilityReads") private RecentResultsCache<AssetAvailabilityReadResult> recentResultsCache;
    
    @Override
    public void readDevices(Set<? extends YukonPao> devices, String resultId) {
        //TEMP CODE!
        final int SLEEP_SECONDS = 5;
        final double SUCCESS_CHANCE = 0.75;
        final Set<Integer> deviceIds = Sets.newHashSet();
        for(YukonPao device : devices) {
            deviceIds.add(device.getPaoIdentifier().getPaoId());
        }
        
        final AssetAvailabilityReadResult result = recentResultsCache.getResult(resultId);
        
        Runnable runnable = new Runnable() {
            public void run() {
                for(int deviceId : deviceIds) {
                    try {
                        Thread.sleep(SLEEP_SECONDS * 1000);
                    } catch(InterruptedException e) {
                        log.error("Test thread interrupted!");
                    }
                    //randomly choose success or failure for next device
                    //(approx. 75% chance of success)
                    double random = Math.random();
                    if(random < SUCCESS_CHANCE) {
                        result.commandSucceeded(deviceId);
                    } else {
                        result.commandFailed(deviceId);
                    }
                }
                log.info("RFN test thread completed.");
            }
        };
        new Thread(runnable).start();
    }
}
