package com.cannontech.dr.assetavailability.ping;

import java.util.List;
import java.util.Set;

import com.cannontech.common.util.Completable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Stores the results of an asset availability "ping" operation.
 */
public class AssetAvailabilityReadResult implements Completable {
    private Set<Integer> validDevices;
    private Set<Integer> invalidDevices;
    private Set<Integer> successDevices = Sets.newHashSet();
    private Set<Integer> failedDevices = Sets.newHashSet();
    private List<String> errorList = Lists.newArrayList();
    
    public AssetAvailabilityReadResult(Iterable<Integer> validDevices, Iterable<Integer> invalidDevices) {
        this.validDevices = Sets.newHashSet(validDevices);
        this.invalidDevices = Sets.newHashSet(invalidDevices);
    }
    
    public void commandSucceeded(int paoId) {
        successDevices.add(paoId);
    }
    
    public void commandFailed(int paoId) {
        failedDevices.add(paoId);
    }
    
    public void errorOccurred(String error) {
        errorList.add(error);
    }
    
    @Override
    public boolean isComplete() {
        return validDevices.size() == getCompletedCount();
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
        return validDevices.size();
    }
    
    public Set<Integer> getInvalidDevices() {
        return invalidDevices;
    }
}
