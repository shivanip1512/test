package com.cannontech.common.rfn.message.tree;

import java.io.Serializable;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.NetworkTreeUpdateTimeResponse
 * 
 * Yukon will receive this message whenever NM refreshes the network tree,
 * either requested by Yukon, scheduled by NM or some other reason.
 * 
 */
public class NetworkTreeUpdateTimeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    // Start time is used in the following cases:
    // 1. (End time - Start time) actually shows how long it took to build the network tree,
    //     which could be used for performance evaluation and statistics collection in the future.
    // 2. By default the network tree is built from 9 days of route data before the Start time.
    // 3. It is the same networkTreeGenerationStart in RfnMetadataMultiResponse.
    private long treeGenerationStartTimeMillis; 
    
    // End time is the time-stamp used by Yukon to track if there is any new Network Tree generated.
    private long treeGenerationEndTimeMillis;
    
    private long nextScheduledRefreshTimeMillis;
    
    private long noForceRefreshBeforeTimeMillis;

    public long getTreeGenerationStartTimeMillis() {
        return treeGenerationStartTimeMillis;
    }

    public void setTreeGenerationStartTimeMillis(long treeGenerationStartTimeMillis) {
        this.treeGenerationStartTimeMillis = treeGenerationStartTimeMillis;
    }

    public long getTreeGenerationEndTimeMillis() {
        return treeGenerationEndTimeMillis;
    }

    public void setTreeGenerationEndTimeMillis(long treeGenerationEndTimeMillis) {
        this.treeGenerationEndTimeMillis = treeGenerationEndTimeMillis;
    }

    public long getNextScheduledRefreshTimeMillis() {
        return nextScheduledRefreshTimeMillis;
    }

    public void setNextScheduledRefreshTimeMillis(long nextScheduledRefreshTimeMillis) {
        this.nextScheduledRefreshTimeMillis = nextScheduledRefreshTimeMillis;
    }

    public long getNoForceRefreshBeforeTimeMillis() {
        return noForceRefreshBeforeTimeMillis;
    }

    public void setNoForceRefreshBeforeTimeMillis(long noForceRefreshBeforeTimeMillis) {
        this.noForceRefreshBeforeTimeMillis = noForceRefreshBeforeTimeMillis;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (nextScheduledRefreshTimeMillis ^ (nextScheduledRefreshTimeMillis >>> 32));
        result = prime * result + (int) (noForceRefreshBeforeTimeMillis ^ (noForceRefreshBeforeTimeMillis >>> 32));
        result = prime * result + (int) (treeGenerationEndTimeMillis ^ (treeGenerationEndTimeMillis >>> 32));
        result = prime * result + (int) (treeGenerationStartTimeMillis ^ (treeGenerationStartTimeMillis >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NetworkTreeUpdateTimeResponse other = (NetworkTreeUpdateTimeResponse) obj;
        if (nextScheduledRefreshTimeMillis != other.nextScheduledRefreshTimeMillis)
            return false;
        if (noForceRefreshBeforeTimeMillis != other.noForceRefreshBeforeTimeMillis)
            return false;
        if (treeGenerationEndTimeMillis != other.treeGenerationEndTimeMillis)
            return false;
        if (treeGenerationStartTimeMillis != other.treeGenerationStartTimeMillis)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format(
                "NetworkTreeUpdateTimeResponse [treeGenerationStartTimeMillis=%s, treeGenerationEndTimeMillis=%s, nextScheduledRefreshTimeMillis=%s, noForceRefreshBeforeTimeMillis=%s]",
                treeGenerationStartTimeMillis, treeGenerationEndTimeMillis, nextScheduledRefreshTimeMillis,
                noForceRefreshBeforeTimeMillis);
    }

}