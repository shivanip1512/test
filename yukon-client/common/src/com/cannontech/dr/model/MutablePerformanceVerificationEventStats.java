package com.cannontech.dr.model;

public class MutablePerformanceVerificationEventStats {

    private int numSuccesses;
    private int numFailures;
    private int numUnknowns;

    public void addStats(int numSuccessesAdd, int numFailuresAdd, int numUnknownsAdd) {
        numSuccesses += numSuccessesAdd;
        numFailures += numFailuresAdd;
        numUnknowns += numUnknownsAdd;
    }

    public PerformanceVerificationEventStats getImmutable() {
        return new PerformanceVerificationEventStats(numSuccesses, numFailures, numUnknowns);
    }
}
