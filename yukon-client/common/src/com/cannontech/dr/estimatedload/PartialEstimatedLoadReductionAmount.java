package com.cannontech.dr.estimatedload;

public class PartialEstimatedLoadReductionAmount {

    private final double connectedLoad;
    private final double diversifiedLoad;
    private final double maxKwSavings;

    public PartialEstimatedLoadReductionAmount(double connectedLoad, double diversifiedLoad, double maxKwSavings) {
        this.connectedLoad = connectedLoad;
        this.diversifiedLoad = diversifiedLoad;
        this.maxKwSavings = maxKwSavings;
    }

    public double getConnectedLoad() {
        return connectedLoad;
    }

    public double getDiversifiedLoad() {
        return diversifiedLoad;
    }

    public double getMaxKwSavings() {
        return maxKwSavings;
    }

}
