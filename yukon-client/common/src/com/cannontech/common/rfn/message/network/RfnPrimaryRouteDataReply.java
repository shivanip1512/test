package com.cannontech.common.rfn.message.network;

import java.io.Serializable;
import java.util.List;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

public class RfnPrimaryRouteDataReply implements RfnIdentifyingMessage, Serializable {

    private static final long serialVersionUID = 1L;
    private RfnIdentifier rfnIdentifier;
    private RfnPrimaryRouteDataReplyType replyType;
    private List<RouteData> routeData;

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public RfnPrimaryRouteDataReplyType getReplyType() {
        return replyType;
    }

    public void setReplyType(RfnPrimaryRouteDataReplyType replyType) {
        this.replyType = replyType;
    }

    public List<RouteData> getRouteData() {
        return routeData;
    }

    public void setRouteData(List<RouteData> routeData) {
        this.routeData = routeData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((replyType == null) ? 0 : replyType.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + ((routeData == null) ? 0 : routeData.hashCode());
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
        RfnPrimaryRouteDataReply other = (RfnPrimaryRouteDataReply) obj;
        if (replyType != other.replyType)
            return false;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (routeData == null) {
            if (other.routeData != null)
                return false;
        } else if (!routeData.equals(other.routeData))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("RfnPrimaryRouteDataReply [rfnIdentifier=%s, replyType=%s, routeData=%s]",
                    rfnIdentifier,
                    replyType,
                    routeData);
    }

}
