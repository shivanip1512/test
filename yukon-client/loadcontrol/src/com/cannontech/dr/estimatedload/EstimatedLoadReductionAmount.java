package com.cannontech.dr.estimatedload;

public final class EstimatedLoadReductionAmount {

    private final Double connectedLoad;
    private final Double diversifiedLoad;
    private final Double maxKwSavings;
    private final Double nowKwSavings;
    
    private final boolean isError;
    private final EstimatedLoadCalculationException exception;

    public EstimatedLoadReductionAmount(Double connectedLoad, Double diversifiedLoad,
            Double maxKwSavings, Double nowKwSavings, boolean isError, EstimatedLoadCalculationException exception) {
        this.connectedLoad = connectedLoad;
        this.diversifiedLoad = diversifiedLoad;
        this.maxKwSavings = maxKwSavings;
        this.nowKwSavings = nowKwSavings;
        this.isError = isError;
        this.exception = exception;
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

    public Double getNowKwSavings() {
        return nowKwSavings;
    }

    public boolean isError() {
        return isError;
    }

    public EstimatedLoadCalculationException getException() {
        return exception;
    }

}
