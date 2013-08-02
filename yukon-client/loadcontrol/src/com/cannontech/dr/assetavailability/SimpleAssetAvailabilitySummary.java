package com.cannontech.dr.assetavailability;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

public class SimpleAssetAvailabilitySummary {
    private final Set<Integer> all = Sets.newHashSet();
    private final Set<Integer> communicating = Sets.newHashSet();
    private final Set<Integer> running = Sets.newHashSet();
    private final Set<Integer> optedOut = Sets.newHashSet();
    
    public SimpleAssetAvailabilitySummary(Collection<Integer> allInventory) {
        all.addAll(allInventory);
    }
    
    public void addCommunicating(Collection<Integer> inventory) {
        communicating.addAll(inventory);
    }
    
    public void addRunning(Collection<Integer> inventory) {
        running.addAll(inventory);
    }
    
    public void addOptedOut(Collection<Integer> inventory) {
        optedOut.addAll(inventory);
    }
    
    public Set<Integer> getAll() {
        return Sets.newHashSet(all);
    }
    
    public Set<Integer> getCommunicatingRunning() {
        Set<Integer> communicatingRunning = Sets.intersection(communicating, running);
        return Sets.difference(communicatingRunning, optedOut);
    }
    
    public Set<Integer> getCommunicatingNotRunning() {
        Set<Integer> communicatingNotRunning = Sets.difference(communicating, running);
        return Sets.difference(communicatingNotRunning, optedOut);
    }
    
    public Set<Integer> getUnavailable() {
        Set<Integer> notCommunicating = Sets.difference(all, communicating);
        Set<Integer> notCommunicatingNotRunning = Sets.difference(notCommunicating, running);
        return Sets.difference(notCommunicatingNotRunning, optedOut);
    }
    
    public Set<Integer> getOptedOut() {
        return Sets.newHashSet(optedOut);
    }
    
    public int getCommunicatingRunningSize(boolean includeOptedOut) {
        Set<Integer> communicatingRunning = Sets.intersection(communicating, running);
        if(includeOptedOut) {
            return communicatingRunning.size();
        }
        return Sets.difference(communicatingRunning, optedOut).size();
    }
    
    public int getCommunicatingRunningSize() {
        return getCommunicatingRunningSize(false);
    }
    
    public int getCommunicatingNotRunningSize(boolean includeOptedOut) {
        Set<Integer> communicatingNotRunning = Sets.difference(communicating, running);
        if(includeOptedOut) {
            return communicatingNotRunning.size();
        }
        return Sets.difference(communicatingNotRunning, optedOut).size();
    }
    
    public int getCommunicatingNotRunningSize() {
        return getCommunicatingNotRunningSize(false);
    }
    
    public int getUnavailableSize(boolean includeOptedOut) {
        Set<Integer> notCommunicating = Sets.difference(all, communicating);
        Set<Integer> notCommunicatingNotRunning = Sets.difference(notCommunicating, running);
        if(includeOptedOut) {
            return notCommunicatingNotRunning.size();
        }
        return Sets.difference(notCommunicatingNotRunning, optedOut).size();
    }
    
    public int getUnavailableSize() {
        return getUnavailableSize(false);
    }
    
    public int getOptedOutSize() {
        return optedOut.size();
    }
}
