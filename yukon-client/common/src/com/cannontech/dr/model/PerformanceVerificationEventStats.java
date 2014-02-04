package com.cannontech.dr.model;

public final class PerformanceVerificationEventStats {

    private final int numSuccesses;
    private final int numFailures;
    private final int numUnknowns;

    public PerformanceVerificationEventStats(int numSuccesses, int numFailures, int numUnknowns) {
        this.numSuccesses = numSuccesses;
        this.numFailures = numFailures;
        this.numUnknowns = numUnknowns;
    }

    public int getNumSuccesses() {
        return numSuccesses;
    }

    public int getNumFailures() {
        return numFailures;
    }

    public int getNumUnknowns() {
        return numUnknowns;
    }

    public double getPercentSuccess() {
        int total = (numSuccesses + numFailures);
        if (total <= 0) {
            return 0.0;
        }
        return (double) numSuccesses / total;
    }
}
