package com.cannontech.amr.porterResponseMonitor.model;

import java.util.Set;

import com.google.common.collect.Sets;

public class PorterResponseMonitorTransaction {
    private int paoId;
    private boolean success;
    private Set<Integer> errorCodes = Sets.newHashSetWithExpectedSize(2);
    
    public PorterResponseMonitorTransaction(int paoId, int errorCode) {
        this.paoId = paoId;
        errorCodes.add(errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public Integer getPaoId() {
        return paoId;
    }
    
    public Set<Integer> getErrorCodes() {
        return errorCodes;
    }
    
    public void addErrorCode(int errorCode) {
        success = errorCode == 0;
        errorCodes.add(errorCode);
    }
    
    @Override
    public String toString() {
        return String.format("PorterResponseMonitorTransaction [paoId=%s, errorCodes=%s]",
                             paoId,
                             errorCodes);
    }
}
