package com.cannontech.thirdparty.digi.model;

import com.cannontech.core.dao.NotFoundException;

public enum NodeStatus {
    
    active(1),
    inactive(0);
    
    private int value;
    
    private NodeStatus(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public boolean isConnected() {
        if (this == active) { 
            return true;
        }
        return false;
    }
    
    public static NodeStatus getNodeStatus(int status) {
        for (NodeStatus nodeStatus : values()) {
            if(nodeStatus.getValue() == status) {
                return nodeStatus;
            }
        }
        throw new NotFoundException("NodeStatus with value: " + status + " was not found.");
    }
}
