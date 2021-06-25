package com.cannontech.simulators.message.request;

import java.io.Serializable;

/**
 * Request to service manager to start simulated device creation
 */
public class EatonCloudDataRetrievalSimulatonRequest implements Serializable{
    private static final long serialVersionUID = 1L;
    private boolean isCreate;
    
    public EatonCloudDataRetrievalSimulatonRequest(boolean isCreate) {
        this.isCreate = isCreate;
    }
    
    public boolean isCreate() {
        return isCreate;
    }
}
