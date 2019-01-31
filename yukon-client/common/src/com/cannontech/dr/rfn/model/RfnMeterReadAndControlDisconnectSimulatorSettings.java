package com.cannontech.dr.rfn.model;

import java.io.Serializable;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;

public class RfnMeterReadAndControlDisconnectSimulatorSettings implements Serializable {

    private RfnMeterDisconnectInitialReplyType disconnectReply1;
    private int disconnectReply1FailPercent = 0;
    private RfnMeterDisconnectConfirmationReplyType disconnectReply2;
    private int disconnectReply2FailPercent = 0;
    private RfnMeterDisconnectState queryResponse;
    
    public RfnMeterReadAndControlDisconnectSimulatorSettings() {
    }
    
    public RfnMeterReadAndControlDisconnectSimulatorSettings(RfnMeterDisconnectInitialReplyType disconnectReply1, int disconnectReply1FailPercent, RfnMeterDisconnectConfirmationReplyType disconnectReply2, int disconnectReply2FailPercent, RfnMeterDisconnectState queryResponse) {
        this.disconnectReply1 = disconnectReply1;
        this.disconnectReply1FailPercent = disconnectReply1FailPercent;
        this.disconnectReply2 = disconnectReply2;
        this.disconnectReply2FailPercent = disconnectReply2FailPercent;
        this.queryResponse = queryResponse;
    }
    
    public RfnMeterDisconnectInitialReplyType getDisconnectReply1() {
        return disconnectReply1;
    }

    public void setDisconnectReply1(RfnMeterDisconnectInitialReplyType disconnectReply1) {
        this.disconnectReply1 = disconnectReply1;
    }

    public int getDisconnectReply1FailPercent() {
        return disconnectReply1FailPercent;
    }

    public void setDisconnectReply1FailPercent(int disconnectReply1FailPercent) {
        this.disconnectReply1FailPercent = disconnectReply1FailPercent;
    }

    public RfnMeterDisconnectConfirmationReplyType getDisconnectReply2() {
        return disconnectReply2;
    }

    public void setDisconnectReply2(RfnMeterDisconnectConfirmationReplyType disconnectReply2) {
        this.disconnectReply2 = disconnectReply2;
    }

    public int getDisconnectReply2FailPercent() {
        return disconnectReply2FailPercent;
    }

    public void setDisconnectReply2FailPercent(int disconnectReply2FailPercent) {
        this.disconnectReply2FailPercent = disconnectReply2FailPercent;
    }

    public RfnMeterDisconnectState getQueryResponse() {
        return queryResponse;
    }

    public void setQueryResponse(RfnMeterDisconnectState queryResponse) {
        this.queryResponse = queryResponse;
    }
    
}
