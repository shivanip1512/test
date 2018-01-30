package com.cannontech.web.capcontrol.models;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;

public class ViewableCapBank implements Comparable<ViewableCapBank>{

    private int ccId;
    private String ccName;
    private int parentId;
    private int cbcId;
    private String cbcName;
    private boolean twoWayCbc;
    private boolean bankMoved;
    private boolean userPerPhaseData;
    private boolean logicalCBC;

    public void setBankInfo(CapBankDevice bank) {
        ccId = bank.getCcId();
        ccName = bank.getCcName();
        parentId = bank.getParentID();
        bankMoved = bank.isBankMoved();
    }

    public void setCbcInfo(LiteYukonPAObject cbc) {
        cbcId = cbc.getLiteID();
        cbcName = cbc.getPaoName();
        logicalCBC = cbc.getPaoType().isLogicalCBC();
    }

    public final int getCcId() {
        return ccId;
    }

    public final String getCcName() {
        return ccName;
    }
    
    public void setCcId(int ccId) {
        this.ccId = ccId;
    }

    public void setCcName(String ccName) {
        this.ccName = ccName;
    }

    public final int getParentId() {
        return parentId;
    }
    
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public final int getCbcId() {
        return cbcId;
    }

    public final String getCbcName() {
        return cbcName;
    }

    public final boolean isTwoWayCbc() {
        return twoWayCbc;
    }
    
    public void setTwoWayCbc(boolean twoWayCbc) {
        this.twoWayCbc = twoWayCbc; 
    }

    public final boolean isBankMoved() {
        return bankMoved;
    }

    @Override
    public int compareTo(ViewableCapBank o) {
        return ccName.compareToIgnoreCase(o.ccName);
    }

    public boolean isUserPerPhaseData() {
        return userPerPhaseData;
    }

    public void setUserPerPhaseData(boolean userPerPhaseData) {
        this.userPerPhaseData = userPerPhaseData;
    }

    public boolean isLogicalCBC() {
        return logicalCBC;
    }

    public void setLogicalCBC(boolean logicalCBC) {
        this.logicalCBC = logicalCBC;
    }
}