package com.cannontech.common.rfn.message.tree;

import java.io.Serializable;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.NetworkTreeUpdateTimeRequest
 */
public class NetworkTreeUpdateTimeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // When set to true, NM will refresh the network tree if the request is after
    // noForceRefreshBeforeTimeMillis. Once the tree is refreshed, NM will send
    // NetworkTreeUpdateTimeResponse. 
    boolean forceRefresh = false;

    public boolean isForceRefresh() {
        return forceRefresh;
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (forceRefresh ? 1231 : 1237);
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
        NetworkTreeUpdateTimeRequest other = (NetworkTreeUpdateTimeRequest) obj;
        if (forceRefresh != other.forceRefresh)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("NetworkTreeUpdateTimeRequest [forceRefresh=%s]", forceRefresh);
    }

}