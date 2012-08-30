package com.cannontech.cbc.cyme.model;

import com.cannontech.common.pao.PaoType;


public class SerializableDictionaryData {
    private String eqNo;
    private String fdrNwId;
    private PhaseInformation phaseA;
    private PhaseInformation phaseB;
    private PhaseInformation phaseC;    
    private PaoType paoType;
    private int ltcTapPosition;
    
    public SerializableDictionaryData(String eqNo, String fdrNwId, 
                                   PhaseInformation phaseA, PhaseInformation phaseB, PhaseInformation phaseC, int ltcTapPosition,
                                   PaoType paoType) {
        this.eqNo = eqNo;
        this.fdrNwId = fdrNwId;
        this.phaseA = phaseA;
        this.phaseB = phaseB;
        this.phaseC = phaseC;
        this.ltcTapPosition = ltcTapPosition;
        this.paoType = paoType;
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
    public PaoType getPaoType() {
        return paoType;
    }
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
    public int getLtcTapPosition() {
        return ltcTapPosition;
    }
    public void setLtcTapPosition(int ltcTapPosition) {
        this.ltcTapPosition = ltcTapPosition;
    }
}
