package com.cannontech.dr.rfn.model;

public class SimulatorSettings {
    
    private final int lcr6200serialFrom;
    private final int lcr6200serialTo;
    private final int lcr6600serialFrom;
    private final int lcr6600serialTo;
    
    public SimulatorSettings() {
        // Default values in case the simulator does not get initialized.
        this.lcr6200serialFrom = 100000;
        this.lcr6200serialTo = 200000;
        this.lcr6600serialFrom = 300000;
        this.lcr6600serialTo = 320000;
    }
    
    public SimulatorSettings(int lcr6200serialFrom, int lcr6200serialTo, int lcr6600serialFrom, int lcr6600serialTo) {
        this.lcr6200serialFrom = lcr6200serialFrom;
        this.lcr6200serialTo = lcr6200serialTo;
        this.lcr6600serialFrom = lcr6600serialFrom;
        this.lcr6600serialTo = lcr6600serialTo;
    }

    public int getLcr6200serialFrom() {
        return lcr6200serialFrom;
    }

    public int getLcr6200serialTo() {
        return lcr6200serialTo;
    }

    public int getLcr6600serialFrom() {
        return lcr6600serialFrom;
    }

    public int getLcr6600serialTo() {
        return lcr6600serialTo;
    }

}
