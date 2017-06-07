package com.cannontech.dr.controlaudit.model;

public class ControlAuditEventStats {
    private final int numConfirmed;
    private final int numUnknowns;

    public ControlAuditEventStats(int numConfirmed, int numUnknowns) {
        this.numConfirmed = numConfirmed;
        this.numUnknowns = numUnknowns;
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
}
