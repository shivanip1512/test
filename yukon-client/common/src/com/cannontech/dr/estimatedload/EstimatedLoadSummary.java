package com.cannontech.dr.estimatedload;


public final class EstimatedLoadSummary implements EstimatedLoadResult {

    private final int totalPrograms;
    private final int contributing;
    private final int calculating;
    private final int errors;
    private final EstimatedLoadAmount summaryAmount;

    public EstimatedLoadSummary(int totalPrograms, int contributing, int calculating, int errors,
            EstimatedLoadAmount summaryAmount) {
        this.totalPrograms = totalPrograms;
        this.contributing = contributing;
        this.calculating = calculating;
        this.errors = errors;
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
}
