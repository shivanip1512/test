package com.cannontech.dr.estimatedload;

import org.springframework.context.MessageSourceResolvable;

public class EstimatedLoadReductionAmount {
    private final Integer programId;

    private final Double connectedLoad;
    private final Double diversifiedLoad;
    private final Double maxKwSavings;
    private final Double nowKwSavings;
    
    private final boolean isError;
    private final MessageSourceResolvable errorMessage;

    public EstimatedLoadReductionAmount(Integer programId, Double connectedLoad, Double diversifiedLoad,
            Double maxKwSavings, Double nowKwSavings, boolean isError, MessageSourceResolvable errorMessage) {
        this.programId = programId;
        this.connectedLoad = connectedLoad;
        this.diversifiedLoad = diversifiedLoad;
        this.maxKwSavings = maxKwSavings;
        this.nowKwSavings = nowKwSavings;
        this.isError = isError;
        this.errorMessage = errorMessage;
    }

    public Integer getProgramId() {
        return programId;
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

    public MessageSourceResolvable getErrorMessage() {
        return errorMessage;
    }

}
