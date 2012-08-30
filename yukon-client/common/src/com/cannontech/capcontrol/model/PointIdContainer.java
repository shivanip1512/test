package com.cannontech.capcontrol.model;

public class PointIdContainer {

    int voltId;
    int varAId;
    int varBId;
    int varCId;
    int varTotalId;
    int wattId;
    
    boolean totalizekVar;
    
    
    public boolean isTotalizekVar() {
        return totalizekVar;
    }
    public void setTotalizekVar(boolean totalizekVar) {
        this.totalizekVar = totalizekVar;
    }
    public int getVoltId() {
        return voltId;
    }
    public void setVoltId(int voltId) {
        this.voltId = voltId;
    }
    public int getVarAId() {
        return varAId;
    }
    public void setVarAId(int varAId) {
        this.varAId = varAId;
    }
    public int getVarBId() {
        return varBId;
    }
    public void setVarBId(int varBId) {
        this.varBId = varBId;
    }
    public int getVarCId() {
        return varCId;
    }
    public void setVarCId(int varCId) {
        this.varCId = varCId;
    }
    public int getVarTotalId() {
        return varTotalId;
    }
    public void setVarTotalId(int varTotalId) {
        this.varTotalId = varTotalId;
    }
    public int getWattId() {
        return wattId;
    }
    public void setWattId(int wattId) {
        this.wattId = wattId;
    }
}
