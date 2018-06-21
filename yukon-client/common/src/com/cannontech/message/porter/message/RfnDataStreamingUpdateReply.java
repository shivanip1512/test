package com.cannontech.message.porter.message;

public class RfnDataStreamingUpdateReply {
    public RfnDataStreamingUpdateReply(boolean success) {
        this.success = success;
    }
    public RfnDataStreamingUpdateReply() {
        this.success = false;
    }
    public boolean success;
}
