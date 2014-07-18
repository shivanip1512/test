package com.cannontech.cbc.cyme.model;

public class SerializableDictionaryData {
    private String eqNo;
    private String fdrNwId;
    private PhaseInformation phaseA;
    private PhaseInformation phaseB;
    private PhaseInformation phaseC;    
    private String eqCode;
    private int ltcTapPosition;
    
    public SerializableDictionaryData(String eqNo, String fdrNwId, 
                                   PhaseInformation phaseA, PhaseInformation phaseB, PhaseInformation phaseC, int ltcTapPosition,
                                   String eqCode) {
        this.eqNo = eqNo;
        this.fdrNwId = fdrNwId;
        this.phaseA = phaseA;
        this.phaseB = phaseB;
        this.phaseC = phaseC;
        this.ltcTapPosition = ltcTapPosition;
        this.eqCode = eqCode;
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
    public String getEqCode() {
        return eqCode;
    }
    public void setEqCode(String eqCode) {
        this.eqCode = eqCode;
    }
    public int getLtcTapPosition() {
        return ltcTapPosition;
    }
    public void setLtcTapPosition(int ltcTapPosition) {
        this.ltcTapPosition = ltcTapPosition;
    }
}
