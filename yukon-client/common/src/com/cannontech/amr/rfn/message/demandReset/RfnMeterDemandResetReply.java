package com.cannontech.amr.rfn.message.demandReset;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.amr.rfn.model.RfnMeterIdentifier;

public class RfnMeterDemandResetReply implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<RfnMeterIdentifier, RfnMeterDemandResetReplyType> replyTypes;

    public Map<RfnMeterIdentifier, RfnMeterDemandResetReplyType> getReplyTypes() {
        return replyTypes;
    }

    public void setReplyTypes(Map<RfnMeterIdentifier, RfnMeterDemandResetReplyType> replyTypes) {
        this.replyTypes = replyTypes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((replyTypes == null) ? 0 : replyTypes.hashCode());
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
        RfnMeterDemandResetReply other = (RfnMeterDemandResetReply) obj;
        if (replyTypes == null) {
            if (other.replyTypes != null)
                return false;
        } else if (!replyTypes.equals(other.replyTypes))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnMeterDemandResetReply [replyTypes=%s]", replyTypes);
    }
}
