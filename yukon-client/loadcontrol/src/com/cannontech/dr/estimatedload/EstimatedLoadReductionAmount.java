package com.cannontech.dr.estimatedload;

public final class EstimatedLoadReductionAmount {

    private final double connectedLoad;
    private final double diversifiedLoad;
    private final double maxKwSavings;
    private final double nowKwSavings;
    
    private final boolean isError;
    private final EstimatedLoadCalculationException exception;

    public EstimatedLoadReductionAmount(double connectedLoad, double diversifiedLoad,
            double maxKwSavings, double nowKwSavings, boolean isError, EstimatedLoadCalculationException exception) {
        this.connectedLoad = connectedLoad;
        this.diversifiedLoad = diversifiedLoad;
        this.maxKwSavings = maxKwSavings;
        this.nowKwSavings = nowKwSavings;
        this.isError = isError;
        this.exception = exception;
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

    public boolean isError() {
        return isError;
    }

    public EstimatedLoadCalculationException getException() {
        return exception;
    }

}
