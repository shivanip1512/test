package com.cannontech.dr.rfn.model;

public class PqrConfigCounts {
    private int inProgress;
    private int failed;
    private int success;
    
    public void addResult(PqrConfigCommandStatus status) {
        switch(status) {
            case IN_PROGRESS: inProgress++; break;
            case FAILED: failed++; break;
            case SUCCESS: success++; break;
        }
    }
    
    public int getInProgress() {
        return inProgress;
    }
    
    public int getFailed() {
        return failed;
    }
    
    public int getSuccess() {
        return success;
    }
}
