package com.cannontech.dr.model;

public final class PerformanceVerificationEventStats {

    private final int success;
    private final int failed;
    private final int unknown;

    public PerformanceVerificationEventStats(int success, int failed, int unknown) {
        this.success = success;
        this.failed = failed;
        this.unknown = unknown;
    }

    public int getSuccess() {
        return success;
    }

    public int getFailed() {
        return failed;
    }

    public int getUnknown() {
        return unknown;
    }

    public double getPercentSuccess() {
        int total = (success + failed);
        if (total <= 0) {
            return 0.0;
        }
        return (double) success / total;
    }
}
