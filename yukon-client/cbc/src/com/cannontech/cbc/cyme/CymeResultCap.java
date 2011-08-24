package com.cannontech.cbc.cyme;

import java.io.StringReader;
import java.util.Properties;

import javax.xml.transform.stream.StreamSource;

import org.jdom.Namespace;

import com.cannontech.common.util.xml.SimpleXPathTemplate;

public class CymeResultCap {
    private String eqNo;
    private String fdrNwId;
    private float iA;
    private float iB;
    private float iC;
    private float vBaseA;
    private float vBaseB;
    private float vBaseC;
    private float kVarA;
    private float kVarB;
    private float kVarC;
    private float kWA;
    private float kWB;
    private float kWC;

    public CymeResultCap(String eqNo, String fdrNwId, float iA, float iB, float iC, float vBaseA, float vBaseB, float vBaseC, float kVarA, float kVarB, float kVarC, float kWA, float kWB, float kWC) {

        this.eqNo = eqNo;
        this.fdrNwId = fdrNwId;
        this.iA = iA;
        this.iB = iB;
        this.iC = iC;
        this.vBaseA = vBaseA;
        this.vBaseB = vBaseB;
        this.vBaseC = vBaseC;
        this.kVarA = kVarA;
        this.kVarB = kVarB;
        this.kVarC = kVarC;
        this.kWA = kWA;
        this.kWB = kWB;
        this.kWC = kWC;

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
    public float getIA() {
        return iA;
    }
    public void setIA(float iASet) {
        iA = iASet;
    }
    public float getIB() {
        return iB;
    }
    public void setIB(float iBSet) {
        iB = iBSet;
    }
    public float getIC() {
        return iC;
    }
    public void setIC(float iCset) {
        iC = iCset;
    }
    public float getVBaseA() {
        return vBaseA;
    }
    public void setVBaseA(float vBaseASet) {
        vBaseA = vBaseASet;
    }
    public float getVBaseB() {
        return vBaseB;
    }
    public void setVBaseB(float vBaseBSet) {
        vBaseB = vBaseBSet;
    }
    public float getVBaseC() {
        return vBaseC;
    }
    public void setVBaseC(float vBaseCSet) {
        vBaseC = vBaseCSet;
    }
    public float getKVarA() {
        return kVarA;
    }
    public void setKVarA(float kVarASet) {
        kVarA = kVarASet;
    }
    public float getKVarB() {
        return kVarB;
    }
    public void setKVarB(float kVarBSet) {
        kVarB = kVarBSet;
    }
    public float getKVarC() {
        return kVarC;
    }
    public void setKVarC(float kVarCSet) {
        kVarC = kVarCSet;
    }
    public float getKWA() {
        return kWA;
    }
    public void setKWA(float kWASet) {
        kWA = kWASet;
    }
    public float getKWB() {
        return kWB;
    }
    public void setKWB(float kWBSet) {
        kWB = kWBSet;
    }
    public float getKWC() {
        return kWC;
    }
    public void setKWC(float kWCSet) {
        kWC = kWCSet;
    }


}
