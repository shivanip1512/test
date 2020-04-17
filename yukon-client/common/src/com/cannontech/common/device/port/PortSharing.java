package com.cannontech.common.device.port;

import com.cannontech.database.db.port.CommPort;

public class PortSharing implements DBPersistentConverter<CommPort> {

    private SharedPortType sharedPortType;
    private Integer sharedSocketNumber;

    public SharedPortType getSharedPortType() {
        return sharedPortType;
    }

    public void setSharedPortType(SharedPortType sharedPortType) {
        this.sharedPortType = sharedPortType;
    }

    public Integer getSharedSocketNumber() {
        return sharedSocketNumber;
    }

    public void setSharedSocketNumber(Integer sharedSocketNumber) {
        this.sharedSocketNumber = sharedSocketNumber;
    }

    @Override
    public void buildModel(CommPort port) {
        setSharedPortType(SharedPortType.getSharedPortType(port.getSharedPortType()));
        setSharedSocketNumber(port.getSharedSocketNumber());
    }

    @Override
    public void buildDBPersistent(CommPort port) {
        if (getSharedPortType() != null) {
            port.setSharedPortType(getSharedPortType().getSharedPortTypeString());
        }
        if (getSharedPortType() == SharedPortType.NONE) {
            // This case will be handled when we can change the Shared Port to None through Update
            port.setSharedSocketNumber(CommPort.DEFAULT_SHARED_SOCKET_NUMBER);
        } else if (getSharedSocketNumber() != null) {
            port.setSharedSocketNumber(getSharedSocketNumber());
        }
    }
 
}
