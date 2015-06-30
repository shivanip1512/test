package com.cannontech.cbc.cyme.model;

public class SerializableDictionaryData {
    private String eqNo;
    private String fdrNwId;
    private PhaseInformation phaseA;
    private PhaseInformation phaseB;
    private PhaseInformation phaseC;
    private String eqCode;
    private int ltcTapPosition;
    private float ltcBandwidth;
    private float ltcSetPoint;
    private float regulatorBandwidth;

    public SerializableDictionaryData(String eqNo, String fdrNwId, PhaseInformation phaseA, PhaseInformation phaseB,
            PhaseInformation phaseC, int ltcTapPosition, String eqCode, float ltcBandwidth, float ltcSetPoint,
            float regulatorBandwidth) {
        this.eqNo = eqNo;
        this.fdrNwId = fdrNwId;
        this.phaseA = phaseA;
        this.phaseB = phaseB;
        this.phaseC = phaseC;
        this.ltcTapPosition = ltcTapPosition;
        this.eqCode = eqCode;
        this.ltcBandwidth = ltcBandwidth;
        this.ltcSetPoint = ltcSetPoint;
        this.regulatorBandwidth = regulatorBandwidth;
    }

    public String getEqNo() {
        return eqNo;
    }

    public String getFdrNwId() {
        return fdrNwId;
    }

    public PhaseInformation getPhaseA() {
        return phaseA;
    }

    public PhaseInformation getPhaseB() {
        return phaseB;
    }

    public PhaseInformation getPhaseC() {
        return phaseC;
    }

    public String getEqCode() {
        return eqCode;
    }

    public int getLtcTapPosition() {
        return ltcTapPosition;
    }

    public float getLtcBandwidth() {
        return ltcBandwidth;
    }

    public float getLtcSetPoint() {
        return ltcSetPoint;
    }

    public float getRegulatorBandwidth() {
        return regulatorBandwidth;
    }
}
