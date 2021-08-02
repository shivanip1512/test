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
    
    public double getTotal() {
        int total = (numConfirmed + numConfirmedAfterRetry + numFailed + numUnknowns);
        if (total <= 0) {
            return 0.0;
        }
        return (double) total;
    }

    public double getPercentConfirmed() {
        return (double) numConfirmed / getTotal();
    }
    
    public double getPercentSuccessAfterRetry() {
        return (double) numConfirmedAfterRetry / getTotal();
    }
    
    public double getPercentFailed() {
        return (double) numFailed / getTotal();
    }

    public double getPercentUnknown() {
        return (double) numUnknowns / getTotal();
    }

    public int getNumFailed() {
        return numFailed;
    }

    public int getNumConfirmedAfterRetry() {
        return numConfirmedAfterRetry;
    }
}