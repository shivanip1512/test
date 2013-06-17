package com.cannontech.web.capcontrol.models;

import com.cannontech.message.capcontrol.streamable.CapBankDevice;

public class MovedBank {
    
    private CapBankDevice capbank = null;
    private String currentFeederName = "";
    private String originalFeederName = "";
    
    public MovedBank(CapBankDevice capbank) {
        this.capbank = capbank;
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

    public CapBankDevice getCapbank() {
        return capbank;
    }
}
