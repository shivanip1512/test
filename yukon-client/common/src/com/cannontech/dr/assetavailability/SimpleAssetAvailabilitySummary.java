package com.cannontech.dr.assetavailability;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * This object contains asset availability information for a set of inventory. It can provide the inventoryIds for all 
 * inventory in a given asset availability state, all opted-out inventory, and the intersections and differences of those
 * sets.
 */
public class SimpleAssetAvailabilitySummary {
    private final Set<Integer> all = Sets.newHashSet();
    private final Set<Integer> communicating = Sets.newHashSet();
    private final Set<Integer> running = Sets.newHashSet();
    private final Set<Integer> optedOut = Sets.newHashSet();
    private Integer unavailableSize = null;
    private Integer inactiveSize = null;
    private Integer activeSize = null;
    
    /**
     * The object must be populated with the complete set of inventoryIds on creation.
     */
    public SimpleAssetAvailabilitySummary(Collection<Integer> allInventory) {
        all.addAll(allInventory);
    }
    
    /**
     * Add inventoryIds to the set of communicating inventory.
     */
    public void addCommunicating(Collection<Integer> inventory) {
        communicating.addAll(inventory);
    }
    
    /**
     * Add inventoryIds to the set of running inventory.
     */
    public void addRunning(Collection<Integer> inventory) {
        running.addAll(inventory);
    }
    
    /**
     * Add inventoryIds to the set of opted-out inventory.
     */
    public void addOptedOut(Collection<Integer> inventory) {
        optedOut.addAll(inventory);
    }
    
    /**
     * Gets inventoryIds for all inventory in this summary.
     */
    public Set<Integer> getAll() {
        return Sets.newHashSet(all);
    }
    
    /**
     * Gets inventoryIds for all inventory that are both communicating and running. This does not include inventory that 
     * are opted-out.
     */
    public Set<Integer> getActive() {
        Set<Integer> communicatingRunning = Sets.intersection(communicating, running);
        return Sets.difference(communicatingRunning, optedOut);
    }
    
    /**
     * Gets inventoryIds for all inventory that are communicating but not running. This does not include inventory that 
     * are opted-out.
     */
    public Set<Integer> getInactive() {
        Set<Integer> communicatingNotRunning = Sets.difference(communicating, running);
        return Sets.difference(communicatingNotRunning, optedOut);
    }
    
    /**
     * Gets inventoryIds for all inventory that are not communicating. This does not include inventory that are opted 
     * out.
     */
    public Set<Integer> getUnavailable() {
        Set<Integer> notCommunicating = Sets.difference(all, communicating);
        Set<Integer> notCommunicatingNotRunning = Sets.difference(notCommunicating, running);
        return Sets.difference(notCommunicatingNotRunning, optedOut);
    }
    
    /**
     * Gets the total number of inventory that are opted-out.
     */
    public Set<Integer> getOptedOut() {
        return Sets.newHashSet(optedOut);
    }
    
    /**
     * Gets the total number of inventory that are both communicating and running.
     * @param includeOptedOut Determines if the count includes inventory that are opted-out.
     */
    public int getActiveSize(boolean includeOptedOut) {
        Set<Integer> communicatingRunning = Sets.intersection(communicating, running);
        if(includeOptedOut) {
            return communicatingRunning.size();
        }
        return Sets.difference(communicatingRunning, optedOut).size();
    }
    
    /**
     * Gets the total number of inventory that are both communicating and running. This does not include inventory that 
     * are opted-out.
     */
    public int getActiveSize() {
        if(activeSize == null) {
            activeSize = getActiveSize(false);
        }
        return activeSize;
    }
    
    /**
     * Gets the total number of inventory that are communicating, but not running.
     * @param includeOptedOut Determines if the count includes inventory that are opted-out.
     */
    public int getInactiveSize(boolean includeOptedOut) {
        Set<Integer> communicatingNotRunning = Sets.difference(communicating, running);
        if(includeOptedOut) {
            return communicatingNotRunning.size();
        }
        return Sets.difference(communicatingNotRunning, optedOut).size();
    }
    
    /**
     * Gets the total number of inventory that are communicating, but not running. This does not include inventory that are
     * opted out.
     */
    public int getInactiveSize() {
        if(inactiveSize == null) {
            inactiveSize = getInactiveSize(false);
        }
        return inactiveSize;
    }
    
    /**
     * Gets the total number of inventory that are unavailable (not communicating).
     * @param includeOptedOut Determines if the count includes inventory that are opted-out.
     */
    public int getUnavailableSize(boolean includeOptedOut) {
        Set<Integer> notCommunicating = Sets.difference(all, communicating);
        if(includeOptedOut) {
            return notCommunicating.size();
        }
        return Sets.difference(notCommunicating, optedOut).size();
    }
    
    /**
     * Gets the total number of inventory that are unavailable (not communicating). This does not include inventory that
     * are opted-out.
     */
    public int getUnavailableSize() {
        if(unavailableSize == null) {
            unavailableSize = getUnavailableSize(false);
        }
        return unavailableSize;
    }
    
    /**
     * Gets the total number of opted-out inventory.
     */
    public int getOptedOutSize() {
        return optedOut.size();
    }
}
