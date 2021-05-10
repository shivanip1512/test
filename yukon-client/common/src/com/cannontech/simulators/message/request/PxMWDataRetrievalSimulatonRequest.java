package com.cannontech.simulators.message.request;

import java.io.Serializable;

/**
 * Request to service manager to start simulated device creation
 */
public class PxMWDataRetrievalSimulatonRequest implements Serializable{
    private static final long serialVersionUID = 1L;
    private boolean isCreate;
    
    public PxMWDataRetrievalSimulatonRequest(boolean isCreate) {
        this.isCreate = isCreate;
    }
    
    public boolean isCreate() {
        return isCreate;
    }
}
