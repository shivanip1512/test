package com.cannontech.web.capcontrol.models;

import com.cannontech.message.capcontrol.streamable.CapBankDevice;

public class MovedBank {

    private int ccId;
    private String ccName;
    private String currentFeederName;
    private String originalFeederName;
    private Integer currentSubstationId;
    private Integer originalSubstationId;

    public MovedBank(CapBankDevice bank) {
        ccId = bank.getCcId();
        ccName = bank.getCcName();
    }

    public final int getCcId() {
        return ccId;
    }

    public final String getCcName() {
        return ccName;
    }

    public String getCurrentFeederName() {
        return currentFeederName;
    }

    public void setCurrentFeederName(String currentFeederName) {
        this.currentFeederName = currentFeederName;
    }

    public String getOriginalFeederName() {
        return originalFeederName;
    }

    public void setOriginalFeederName(String originalFeederName) {
        this.originalFeederName = originalFeederName;
    }

    public int getCurrentSubstationId() {
        return currentSubstationId;
    }

    public void setCurrentSubstationId(int currentSubstationId) {
        this.currentSubstationId = currentSubstationId;
    }

    public int getOriginalSubstationId() {
        return originalSubstationId;
    }

    public void setOriginalSubstationId(int originalSubstationId) {
        this.originalSubstationId = originalSubstationId;
    }
}