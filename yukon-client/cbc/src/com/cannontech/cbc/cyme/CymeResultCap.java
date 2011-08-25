package com.cannontech.cbc.cyme;

import com.cannontech.cbc.cyme.impl.PhaseInformation;

public class CymeResultCap {
    private String eqNo;
    private String fdrNwId;
    private PhaseInformation phaseA;
    private PhaseInformation phaseB;
    private PhaseInformation phaseC;    

    public CymeResultCap(String eqNo, String fdrNwId, PhaseInformation phaseA,
                         PhaseInformation phaseB, PhaseInformation phaseC) {
        this.eqNo = eqNo;
        this.fdrNwId = fdrNwId;
        this.setPhaseA(phaseA);
        this.setPhaseB(phaseB);
        this.setPhaseC(phaseC);
    }
    public String getEqNo() {
        return eqNo;
    }
    public void setEqNo(String eqNum) {
        eqNo = eqNum;
    }
    public String getFdrNwId() {
        return fdrNwId;
    }
    public void setFdrNwId(String fdrNwId) {
        this.fdrNwId = fdrNwId;
    }
    public void setPhaseA(PhaseInformation phaseA) {
        this.phaseA = phaseA;
    }
    public PhaseInformation getPhaseA() {
        return phaseA;
    }
    public void setPhaseB(PhaseInformation phaseB) {
        this.phaseB = phaseB;
    }
    public PhaseInformation getPhaseB() {
        return phaseB;
    }
    public void setPhaseC(PhaseInformation phaseC) {
        this.phaseC = phaseC;
    }
    public PhaseInformation getPhaseC() {
        return phaseC;
    }


}
