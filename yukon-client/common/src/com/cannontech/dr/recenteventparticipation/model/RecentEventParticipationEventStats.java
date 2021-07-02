package com.cannontech.dr.recenteventparticipation.model;

public class RecentEventParticipationEventStats {
    private final int numConfirmed;
    private final int numUnknowns;
    private final int numFailed;
    private final int numConfirmedAfterRetry;

    public RecentEventParticipationEventStats(int numConfirmed, int numUnknowns, int numFailed, int numConfirmedAfterRetry) {
        this.numConfirmed = numConfirmed;
        this.numUnknowns = numUnknowns;
        this.numFailed = numFailed;
        this.numConfirmedAfterRetry = numConfirmedAfterRetry;
    }

    public int getNumConfirmed() {
        return numConfirmed;
    }

    public int getNumUnknowns() {
        return numUnknowns;
    }

    public double getPercentConfirmed() {
        int total = (numConfirmed + numUnknowns);
        if (total <= 0) {
            return 0.0;
        }
        return (double) numConfirmed / total;
    }

    public double getPercentUnknown() {
        int total = (numConfirmed + numUnknowns);
        if (total <= 0) {
            return 0.0;
        }
        return (double) numUnknowns / total;
    }

    public int getNumFailed() {
        return numFailed;
    }

    public int getNumConfirmedAfterRetry() {
        return numConfirmedAfterRetry;
    }
}