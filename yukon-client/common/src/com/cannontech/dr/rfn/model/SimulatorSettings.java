package com.cannontech.dr.rfn.model;

public class SimulatorSettings {
    
    private final int lcr6200serialFrom;
    private final int lcr6200serialTo;
    private final int lcr6600serialFrom;
    private final int lcr6600serialTo;
    private final long messageId;
    private final long messageIdTimestamp;
    private final double daysBehind;
    

    public SimulatorSettings(int lcr6200serialFrom, int lcr6200serialTo, int lcr6600serialFrom, int lcr6600serialTo,
            long messageId, long messageIdTimestamp, double daysBehind) {
        this.lcr6200serialFrom = lcr6200serialFrom;
        this.lcr6200serialTo = lcr6200serialTo;
        this.lcr6600serialFrom = lcr6600serialFrom;
        this.lcr6600serialTo = lcr6600serialTo;
        this.messageId = messageId;
        this.messageIdTimestamp = messageIdTimestamp;
        this.daysBehind = daysBehind;
    }

    public int getLcr6200serialFrom() {
        return lcr6200serialFrom;
    }

    public int getLcr6200serialTo() {
        return lcr6200serialTo;
    }

    public int getLcr6600serialFrom() {
        return lcr6600serialFrom;
    }

    public int getLcr6600serialTo() {
        return lcr6600serialTo;
    }

    public long getMessageId() {
        return messageId;
    }

    public long getMessageIdTimestamp() {
        return messageIdTimestamp;
    }

    public double getDaysBehind() {
        return daysBehind;
    }
}
