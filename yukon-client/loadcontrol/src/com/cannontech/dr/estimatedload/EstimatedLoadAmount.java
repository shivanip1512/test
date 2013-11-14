package com.cannontech.dr.estimatedload;

public final class EstimatedLoadAmount implements EstimatedLoadResult {

    private final double connectedLoad;
    private final double diversifiedLoad;
    private final double maxKwSavings;
    private final double nowKwSavings;

    public EstimatedLoadAmount(double connectedLoad, double diversifiedLoad, double maxKwSavings, double nowKwSavings) {
        this.connectedLoad = connectedLoad;
        this.diversifiedLoad = diversifiedLoad;
        this.maxKwSavings = maxKwSavings;
        this.nowKwSavings = nowKwSavings;
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

    public double getNowKwSavings() {
        return nowKwSavings;
    }

}
