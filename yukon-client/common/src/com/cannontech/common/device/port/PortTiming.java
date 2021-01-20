package com.cannontech.common.device.port;

public class PortTiming implements DBPersistentConverter<com.cannontech.database.db.port.PortTiming> {

    private Integer preTxWait;
    private Integer rtsToTxWait;
    private Integer postTxWait;
    private Integer receiveDataWait;
    private Integer extraTimeOut;
    private Integer postCommWait;

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

    public Integer getPostCommWait() {
        return postCommWait;
    }

    public void setPostCommWait(Integer postCommWait) {
        this.postCommWait = postCommWait;
    }

    @Override
    public void buildModel(com.cannontech.database.db.port.PortTiming portTiming) {
        setExtraTimeOut(portTiming.getExtraTimeOut());
        setPostTxWait(portTiming.getPostTxWait());
        setPreTxWait(portTiming.getPreTxWait());
        setReceiveDataWait(portTiming.getReceiveDataWait());
        setRtsToTxWait(portTiming.getRtsToTxWait());
        setPostCommWait(portTiming.getPostCommWait());
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.db.port.PortTiming portTiming) {
        if (getExtraTimeOut() != null) {
            portTiming.setExtraTimeOut(getExtraTimeOut());
        }
        if (getPostTxWait() != null) {
            portTiming.setPostTxWait(getPostTxWait());
        }
        if (getPreTxWait() != null) {
            portTiming.setPreTxWait(getPreTxWait());
        }
        if (getReceiveDataWait() != null) {
            portTiming.setReceiveDataWait(getReceiveDataWait());
        }
        if (getRtsToTxWait() != null) {
            portTiming.setRtsToTxWait(getRtsToTxWait());
        }
        if (getPostCommWait() != null) {
            portTiming.setPostCommWait(getPostCommWait());
        }
    }
}
