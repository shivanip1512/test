package com.cannontech.dr.rfn.model;

import java.io.Serializable;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;

public class RfnMeterReadAndControlReadSimulatorSettings implements Serializable{

    private RfnMeterReadingReplyType readReply1;
    private int readReply1FailPercent = 0;
    private RfnMeterReadingDataReplyType readReply2;
    private int readReply2FailPercent = 0;
    
    public RfnMeterReadAndControlReadSimulatorSettings() {
    }
    
    public RfnMeterReadAndControlReadSimulatorSettings(RfnMeterReadingReplyType readReply1, int readReply1FailPercent, RfnMeterReadingDataReplyType readReply2, int readReply2FailPercent) {
        this.readReply1 = readReply1;
        this.readReply1FailPercent = readReply1FailPercent;
        this.readReply2 = readReply2;
        this.readReply2FailPercent = readReply2FailPercent;
    }
    
    public RfnMeterReadingReplyType getReadReply1() {
        return readReply1;
    }

    public void setReadReply1(RfnMeterReadingReplyType readReply1) {
        this.readReply1 = readReply1;
    }

    public int getReadReply1FailPercent() {
        return readReply1FailPercent;
    }

    public void setReadReply1FailPercent(int readReply1FailPercent) {
        this.readReply1FailPercent = readReply1FailPercent;
    }

    public RfnMeterReadingDataReplyType getReadReply2() {
        return readReply2;
    }

    public void setReadReply2(RfnMeterReadingDataReplyType readReply2) {
        this.readReply2 = readReply2;
    }

    public int getReadReply2FailPercent() {
        return readReply2FailPercent;
    }

    public void setReadReply2FailPercent(int readReply2FailPercent) {
        this.readReply2FailPercent = readReply2FailPercent;
    }
}
