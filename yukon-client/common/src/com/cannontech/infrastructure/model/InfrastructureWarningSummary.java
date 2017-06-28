package com.cannontech.infrastructure.model;

import org.joda.time.Instant;

/**
 * A summary, containing the total number of each warnable device category in the system, and the number of each that
 * actually have warnings.
 */
public class InfrastructureWarningSummary {
    private int totalGateways;
    private int warningGateways;
    private int totalRelays;
    private int warningRelays;
    private int totalCcus;
    private int warningCcus;
    private int totalRepeaters;
    private int warningRepeaters;
    private Instant lastRun;
    
    public int getTotalGateways() {
        return totalGateways;
    }
    
    public void setTotalGateways(int totalGateways) {
        this.totalGateways = totalGateways;
    }
    
    public int getWarningGateways() {
        return warningGateways;
    }
    
    public void setWarningGateways(int warningGateways) {
        this.warningGateways = warningGateways;
    }
    
    public int getTotalRelays() {
        return totalRelays;
    }
    
    public void setTotalRelays(int totalRelays) {
        this.totalRelays = totalRelays;
    }
    
    public int getWarningRelays() {
        return warningRelays;
    }
    
    public void setWarningRelays(int warningRelays) {
        this.warningRelays = warningRelays;
    }
    
    public int getTotalCcus() {
        return totalCcus;
    }
    
    public void setTotalCcus(int totalCcus) {
        this.totalCcus = totalCcus;
    }
    
    public int getWarningCcus() {
        return warningCcus;
    }
    
    public void setWarningCcus(int warningCcus) {
        this.warningCcus = warningCcus;
    }
    
    public int getTotalRepeaters() {
        return totalRepeaters;
    }
    
    public void setTotalRepeaters(int totalRepeaters) {
        this.totalRepeaters = totalRepeaters;
    }
    
    public int getWarningRepeaters() {
        return warningRepeaters;
    }
    
    public void setWarningRepeaters(int warningRepeaters) {
        this.warningRepeaters = warningRepeaters;
    }
    
    public Instant getLastRun() {
        return lastRun;
    }
    
    public void setLastRun(Instant lastRun) {
        this.lastRun = lastRun;
    }
    
    @Override
    public String toString() {
        return "InfrastructureWarningSummary [totalGateways=" + totalGateways + ", warningGateways=" + warningGateways
               + ", totalRelays=" + totalRelays + ", warningRelays=" + warningRelays + ", totalCcus=" + totalCcus
               + ", warningCcus=" + warningCcus + ", totalRepeaters=" + totalRepeaters + ", warningRepeaters="
               + warningRepeaters + ", lastRun=" + lastRun + "]";
    }
    
}
