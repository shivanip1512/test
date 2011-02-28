package com.cannontech.amr.monitors.message;


public class PorterResponseMessage {
    private int userMessageId;
    private long connectionId;
    private int paoId;
    private int errorCode;
    private boolean finalMsg;

    public PorterResponseMessage() {
    }
    
    public PorterResponseMessage(int userMessageId, long connectionId, int paoId, int errorCode, boolean finalMsg) {
        this.userMessageId = userMessageId;
        this.connectionId = connectionId;
        this.paoId = paoId;
        this.errorCode = errorCode;
        this.finalMsg = finalMsg;
    }

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
    public long getConnectionId() {
        return connectionId;
    }
    public void setConnectionId(long connectionId) {
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