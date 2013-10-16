package com.cannontech.dr.estimatedload;

public final class PartialEstimatedLoadReductionAmount {

    private final Double connectedLoad;
    private final Double diversifiedLoad;
    private final Double maxKwSavings;

    public PartialEstimatedLoadReductionAmount(Double connectedLoad, Double diversifiedLoad, Double maxKwSavings) {
        this.connectedLoad = connectedLoad;
        this.diversifiedLoad = diversifiedLoad;
        this.maxKwSavings = maxKwSavings;
    }

    public Double getConnectedLoad() {
        return connectedLoad;
    }

    public Double getDiversifiedLoad() {
        return diversifiedLoad;
    }

    public Double getMaxKwSavings() {
        return maxKwSavings;
    }

}
