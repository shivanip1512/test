package com.cannontech.dr.rfn.message.broadcast;

import java.io.Serializable;
import java.util.Map;

/**
 * JMS Queue name: JMSReplyTo of yukon.qr.obj.dr.rfn.ExpressComBroadcastRequest
 */
public class RfnExpressComBroadcastReply implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Map<Long, RfnExpressComBroadcastReplyType> status;

    public void setStatus(Map<Long, RfnExpressComBroadcastReplyType> status) {
        this.status = status;
    }

    public Map<Long, RfnExpressComBroadcastReplyType> getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("RfnExpressComBroadcastReply [status=%s]", status);
    }

    
}