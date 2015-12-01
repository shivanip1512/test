package com.cannontech.development.model;

public class DataSimulatorParameters {
    int lcr6200serialFrom;
    int lcr6200serialTo;
    int lcr6600serialFrom;
    int lcr6600serialTo;
    long messageId;
    long messageIdTimestamp;
    double daysBehind;

    public int getLcr6200serialFrom() {
        return lcr6200serialFrom;
    }

    public void setLcr6200serialFrom(int lcr6200serialFrom) {
        this.lcr6200serialFrom = lcr6200serialFrom;
    }

    public int getLcr6200serialTo() {
        return lcr6200serialTo;
    }

    public void setLcr6200serialTo(int lcr6200serialTo) {
        this.lcr6200serialTo = lcr6200serialTo;
    }

    public int getLcr6600serialFrom() {
        return lcr6600serialFrom;
    }

    public void setLcr6600serialFrom(int lcr6600serialFrom) {
        this.lcr6600serialFrom = lcr6600serialFrom;
    }

    public int getLcr6600serialTo() {
        return lcr6600serialTo;
    }

    public void setLcr6600serialTo(int lcr6600serialTo) {
        this.lcr6600serialTo = lcr6600serialTo;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageIdTimestamp() {
        return messageIdTimestamp;
    }

    public void setMessageIdTimestamp(long messageIdTimestamp) {
        this.messageIdTimestamp = messageIdTimestamp;
    }

    public double getDaysBehind() {
        return daysBehind;
    }

    public void setDaysBehind(double daysBehind) {
        this.daysBehind = daysBehind;
    }

}
