package com.cannontech.infrastructure.model;

import java.util.Map;
import java.util.function.IntSupplier;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableMap;

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
    
    private final Map<InfrastructureWarningDeviceCategory, IntSupplier> deviceCategoryTotalSuppliers = 
        ImmutableMap.of(
            InfrastructureWarningDeviceCategory.GATEWAY, this::getTotalGateways,
            InfrastructureWarningDeviceCategory.RELAY, this::getTotalRelays,
            InfrastructureWarningDeviceCategory.CCU, this::getTotalCcus,
            InfrastructureWarningDeviceCategory.REPEATER, this::getTotalRepeaters
        );
    
    private final Map<InfrastructureWarningDeviceCategory, IntSupplier> deviceCategoryWarningSuppliers = 
        ImmutableMap.of(
            InfrastructureWarningDeviceCategory.GATEWAY, this::getWarningGateways,
            InfrastructureWarningDeviceCategory.RELAY, this::getWarningRelays,
            InfrastructureWarningDeviceCategory.CCU, this::getWarningCcus,
            InfrastructureWarningDeviceCategory.REPEATER, this::getWarningRepeaters
        );
    
    public int getTotalDevices(InfrastructureWarningDeviceCategory category) {
        return deviceCategoryTotalSuppliers.get(category).getAsInt();
    }
    
    public int getWarningDevices(InfrastructureWarningDeviceCategory category) {
        return deviceCategoryWarningSuppliers.get(category).getAsInt();
    }
    
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
    
    /** Used in JSP **/
    public int getDevicesWithWarningsCount() {
        return warningGateways + warningRelays + warningCcus + warningRepeaters;
    }

    /** Used in JSP **/
    public int getDevicesWithoutWarningsCount() {
        return totalGateways - warningGateways +
               totalRelays - warningRelays +
               totalCcus - warningCcus +
               totalRepeaters - warningRepeaters;
    }
    
    @Override
    public String toString() {
        return "InfrastructureWarningSummary [totalGateways=" + totalGateways + ", warningGateways=" + warningGateways
               + ", totalRelays=" + totalRelays + ", warningRelays=" + warningRelays + ", totalCcus=" + totalCcus
               + ", warningCcus=" + warningCcus + ", totalRepeaters=" + totalRepeaters + ", warningRepeaters="
               + warningRepeaters + ", lastRun=" + lastRun + "]";
    }
    
    public InfrastructureWarningSummary copy() {
        InfrastructureWarningSummary copy = new InfrastructureWarningSummary();
        copy.setLastRun(lastRun);
        copy.setTotalCcus(totalCcus);
        copy.setTotalGateways(totalGateways);
        copy.setTotalRelays(totalRelays);
        copy.setTotalRepeaters(totalRepeaters);
        copy.setWarningCcus(warningCcus);
        copy.setWarningGateways(warningGateways);
        copy.setWarningRelays(warningRelays);
        copy.setWarningRepeaters(warningRepeaters);
        return copy;
    }
}
