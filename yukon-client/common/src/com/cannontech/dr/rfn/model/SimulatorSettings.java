package com.cannontech.dr.rfn.model;

public class SimulatorSettings {
    private int lcr6200serialFrom;
    private int lcr6200serialTo;
    private int lcr6600serialFrom;
    private int lcr6600serialTo;
    //% of duplicates to generate
    private int precentOfDuplicates;
    
    public SimulatorSettings(int lcr6200serialFrom, int lcr6200serialTo, int lcr6600serialFrom, int lcr6600serialTo, int precentOfDuplicates) {
        this.lcr6200serialFrom = lcr6200serialFrom;
        this.lcr6200serialTo = lcr6200serialTo;
        this.lcr6600serialFrom = lcr6600serialFrom;
        this.lcr6600serialTo = lcr6600serialTo;
        this.precentOfDuplicates =  precentOfDuplicates;
    }
    
    public SimulatorSettings() {
    }
    public int getLcr6200serialFrom() {
        return lcr6200serialFrom;
    }
    public void setLcr6200serialFrom(int lcr6200serialFrom) {
        this.lcr6200serialFrom = lcr6200serialFrom;
    }
    public int getLcr6200serialTo() {
        return lcr6200serialTo;
    }
    public void setLcr6200serialTo(int lcr6200serialTo) {
        this.lcr6200serialTo = lcr6200serialTo;
    }
    public int getLcr6600serialFrom() {
        return lcr6600serialFrom;
    }
    public void setLcr6600serialFrom(int lcr6600serialFrom) {
        this.lcr6600serialFrom = lcr6600serialFrom;
    }
    public int getLcr6600serialTo() {
        return lcr6600serialTo;
    }
    public void setLcr6600serialTo(int lcr6600serialTo) {
        this.lcr6600serialTo = lcr6600serialTo;
    }
    public int getPrecentOfDuplicates() {
        return precentOfDuplicates;
    }
    public void setPrecentOfDuplicates(int precentOfDuplicates) {
        this.precentOfDuplicates = precentOfDuplicates;
    }
}
