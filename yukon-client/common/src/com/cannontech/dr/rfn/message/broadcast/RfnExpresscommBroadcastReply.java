package com.cannontech.dr.rfn.message.broadcast;

import java.io.Serializable;
import java.util.Map;

/**
 * JMS Queue name: JMSReplyTo of yukon.qr.obj.dr.rfn.ExpresscommBroadcastRequest
 */
public class RfnExpresscommBroadcastReply implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Map<Long, RfnExpresscommBroadcastReplyType> status;

    public void setStatus(Map<Long, RfnExpresscommBroadcastReplyType> status) {
        this.status = status;
    }

    public Map<Long, RfnExpresscommBroadcastReplyType> getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("RfnExpresscommBroadcastReply [status=%s]", status);
    }

}