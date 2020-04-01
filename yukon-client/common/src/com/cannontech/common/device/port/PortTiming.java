package com.cannontech.common.device.port;

public class PortTiming {

    private Integer preTxWait;
    private Integer rtsToTxWait;
    private Integer postTxWait;
    private Integer receiveDataWait;
    private Integer extraTimeOut;

    public Integer getPreTxWait() {
        return preTxWait;
    }

    public void setPreTxWait(Integer preTxWait) {
        this.preTxWait = preTxWait;
    }

    public Integer getRtsToTxWait() {
        return rtsToTxWait;
    }

    public void setRtsToTxWait(Integer rtsToTxWait) {
        this.rtsToTxWait = rtsToTxWait;
    }

    public Integer getPostTxWait() {
        return postTxWait;
    }

    public void setPostTxWait(Integer postTxWait) {
        this.postTxWait = postTxWait;
    }

    public Integer getReceiveDataWait() {
        return receiveDataWait;
    }

    public void setReceiveDataWait(Integer receiveDataWait) {
        this.receiveDataWait = receiveDataWait;
    }

    public Integer getExtraTimeOut() {
        return extraTimeOut;
    }

    public void setExtraTimeOut(Integer extraTimeOut) {
        this.extraTimeOut = extraTimeOut;
    }

    public void buildModel(com.cannontech.database.db.port.PortTiming portTiming) {
        setExtraTimeOut(portTiming.getExtraTimeOut());
        setPostTxWait(portTiming.getPostTxWait());
        setPreTxWait(portTiming.getPreTxWait());
        setReceiveDataWait(portTiming.getReceiveDataWait());
        setRtsToTxWait(portTiming.getRtsToTxWait());
    }
}
