package com.cannontech.dr.estimatedload;

import java.util.Set;

import com.cannontech.dr.estimatedload.service.impl.EstimatedLoadBackingServiceHelperImpl.ButtonInfo;


public final class EstimatedLoadSummary implements EstimatedLoadResult {

    private final int totalPrograms;
    private final int contributing;
    private final int calculating;
    private final int errors;
    private final Set<Integer> programsInError;
    private final EstimatedLoadAmount summaryAmount;
    private final ButtonInfo buttonInfo;

    public EstimatedLoadSummary(int totalPrograms, int contributing, int calculating, int errors, 
            Set<Integer> programsInError, ButtonInfo buttonInfo, EstimatedLoadAmount summaryAmount) {
        this.totalPrograms = totalPrograms;
        this.contributing = contributing;
        this.calculating = calculating;
        this.errors = errors;
        this.programsInError = programsInError;
        this.buttonInfo = buttonInfo;
        this.summaryAmount = summaryAmount;
    }

    public int getTotalPrograms() {
        return totalPrograms;
    }

    public int getContributing() {
        return contributing;
    }

    public int getCalculating() {
        return calculating;
    }

    public int getErrors() {
        return errors;
    }

    public EstimatedLoadAmount getSummaryAmount() {
        return summaryAmount;
    }
    
    public Set<Integer> getProgramsInError() {
        return programsInError;
    }

    public ButtonInfo getButtonInfo() {
        return buttonInfo;
    }
}
