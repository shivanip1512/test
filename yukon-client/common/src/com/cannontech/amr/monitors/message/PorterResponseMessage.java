package com.cannontech.amr.monitors.message;


public class PorterResponseMessage {
    private int userMessageId;
    private int connectionId;
    private int paoId;
    private int errorCode;
    private boolean finalMsg;

    /**
     * used to correlate messages within a connection
     */
    public int getUserMessageId() {
        return userMessageId;
    }
    public void setUserMessageId(int userMessageId) {
        this.userMessageId = userMessageId;
    }
    /**
     * has no meaning (i.e. it can't be looked up in the DB)
     */
    public int getConnectionId() {
        return connectionId;
    }
    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }
    public int getPaoId() {
        return paoId;
    }
    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    public boolean isFinalMsg() {
        return finalMsg;
    }
    public void setFinalMsg(boolean finalMsg) {
        this.finalMsg = finalMsg;
    }
}