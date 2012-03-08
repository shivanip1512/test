package com.cannontech.amr.rfn.message.disconnect;

import java.io.Serializable;

import com.cannontech.database.db.point.stategroup.RFNDisconnectStatusState;

public class RfnMeterDisconnectConfirmationReply implements Serializable {

    private static final long serialVersionUID = 2L;
    private RfnMeterDisconnectConfirmationReplyType replyType = RfnMeterDisconnectConfirmationReplyType.TIMEOUT;
    private RFNDisconnectStatusState state;
    
    public boolean isSuccess() {
        return replyType == RfnMeterDisconnectConfirmationReplyType.SUCCESS;
    }

    public void setReplyType(RfnMeterDisconnectConfirmationReplyType replyType) {
        this.replyType = replyType;
    }
    
    public RfnMeterDisconnectConfirmationReplyType getReplyType() {
        return replyType;
    }
    
    public void setState(RFNDisconnectStatusState state) {
        this.state = state;
    }
    
    public RFNDisconnectStatusState getState() {
        return state;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((replyType == null) ? 0 : replyType.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
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
        RfnMeterDisconnectConfirmationReply other = (RfnMeterDisconnectConfirmationReply) obj;
        if (replyType != other.replyType)
            return false;
        if (state != other.state)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RfnMeterDisconnectConfirmationReply [replyType=" + replyType + ", state=" + state + "]";
    }
    
}