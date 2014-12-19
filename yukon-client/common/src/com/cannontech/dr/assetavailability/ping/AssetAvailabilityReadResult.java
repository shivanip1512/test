package com.cannontech.dr.assetavailability.ping;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.Completable;
import com.cannontech.core.dynamic.PointValueHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Stores the results of an asset availability "ping" operation. Also acts as a DeviceAttributeReadCallback, so that
 * it can directly record results from DeviceAttributeReadService.
 */
public class AssetAvailabilityReadResult implements DeviceAttributeReadCallback, Completable {
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityReadResult.class);
    private final Set<Integer> devicesToRead;
    private final Set<Integer> successDevices = Sets.newHashSet();
    private final Set<Integer> failedDevices = Sets.newHashSet();
    private final List<SpecificDeviceErrorDescription> errorList = Lists.newArrayList();
    
    public AssetAvailabilityReadResult(Iterable<Integer> devicesToRead) {
        this.devicesToRead = Sets.newHashSet(devicesToRead);
    }
    
    @Override
    public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
        int paoId = pao.getPaoId();
        if(isValidDevice(paoId)) {
            successDevices.add(paoId);
            log.debug("Asset availability read received value for paoId " + paoId);
        } else {
            log.debug("Asset availability read received value for invalid paoId " + paoId);
        }
    }

    @Override
    public void receivedLastValue(PaoIdentifier pao, String value) {
        log.debug("Asset availability read received last value for paoId " + pao.getPaoId());
    }

    @Override
    public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
        int paoId = pao.getPaoId();
        if(isValidDevice(pao.getPaoId())) {
            failedDevices.add(pao.getPaoId());
            errorList.add(error);
            log.debug("Asset availability read received error "+error+ "for paoId " + paoId);
        } else {
            log.debug("Asset availability read received error "+error+ "for paoId " + paoId);
        }
    }

    @Override
    public void receivedException(SpecificDeviceErrorDescription error) {
        errorList.add(error);
        log.debug("Asset availability read received error");
    }

    @Override
    public void complete() {
        //Ping is complete - if we haven't heard from some devices, assume we never will and mark as failed.
        Set<Integer> unsuccessfulDevices = Sets.difference(devicesToRead, successDevices);
        Set<Integer> incompleteDevices = Sets.difference(unsuccessfulDevices, failedDevices);
        failedDevices.addAll(incompleteDevices);
    }
    
    @Override
    public boolean isComplete() {
        return devicesToRead.size() == getCompletedCount();
    }
    
    public int getCompletedCount() {
        return successDevices.size() + failedDevices.size();
    }
    
    public int getSuccessCount() {
        return successDevices.size();
    }
    
    public int getFailedCount() {
        return failedDevices.size();
    }
    
    public boolean isErrorOccurred() {
        return errorList.size() > 0;
    }
    
    public int getTotalCount() {
        return devicesToRead.size();
    }
    
    private boolean isValidDevice(int paoId) {
        return devicesToRead.contains(paoId)
                && !successDevices.contains(paoId)
                && !failedDevices.contains(paoId);
    }
}
