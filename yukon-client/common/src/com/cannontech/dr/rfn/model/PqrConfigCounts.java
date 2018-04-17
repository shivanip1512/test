package com.cannontech.dr.rfn.model;

/**
 * An object describing a PQR configuration operation by total count of devices with each status.
 */
public class PqrConfigCounts {
    private int inProgress;
    private int unsupported;
    private int failed;
    private int success;
    
    public void addResult(PqrConfigCommandStatus status) {
        switch(status) {
            case IN_PROGRESS: inProgress++; break;
            case UNSUPPORTED: unsupported++; break;
            case FAILED: failed++; break;
            case SUCCESS: success++; break;
        }
    }
    
    public int getInProgress() {
        return inProgress;
    }
    
    public int getUnsupported() {
        return unsupported;
    }
    
    public int getFailed() {
        return failed;
    }
    
    public int getSuccess() {
        return success;
    }
}
