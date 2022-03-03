package com.cannontech.common.nmHeartbeat.message;

import java.io.Serializable;

public class NetworkManagerHeartbeatResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String messageId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
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
        NetworkManagerHeartbeatResponse other = (NetworkManagerHeartbeatResponse) obj;
        if (messageId == null) {
            if (other.messageId != null)
                return false;
        } else if (!messageId.equals(other.messageId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "NetworkManagerHeartbeatResponse [messageId=" + messageId + "]";
    }
}
