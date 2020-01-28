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

    /**
     * StartTime is used in the following cases:
     * <ul>
     * <li> (EndTime - StartTime) actually shows how long it took to build the network tree,
     *      which can be used for performance evaluation and statistics collection.</li>
     * <li> By default the network tree is built from 9 days of route data before the StartTime.</li>
     * <li> This is also included in RfnMetadataMultiResponse.</li>
     * <li> Either StartTime or EndTime can be used as a tree identifier.
     * </ul>
     */
    private long treeGenerationStartTimeMillis; 
    
    /**
     * EndTime is used in the following cases:
     * <ul>
     * <li> (EndTime - StartTime): See above
     * <li> Yukon will disable the refresh button for half an hour from the EndTime.
     * <li> Either StartTime or EndTime can be used as a tree identifier.
     * <ul> 
     */
    private long treeGenerationEndTimeMillis;
    
    private long nextScheduledRefreshTimeMillis;
    
    private long noForceRefreshBeforeTimeMillis;

    /**
     * 
     * @return the start time (in milliseconds) at which the network tree was generated.
     */
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