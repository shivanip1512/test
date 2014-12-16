package com.cannontech.web.capcontrol.models;

import com.cannontech.database.data.device.DeviceTypesFuncs;
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

    public void setBankInfo(CapBankDevice bank) {
        ccId = bank.getCcId();
        ccName = bank.getCcName();
        parentId = bank.getParentID();
        bankMoved = bank.isBankMoved();
    }

    public void setCbcInfo(LiteYukonPAObject cbc) {
        cbcId = cbc.getLiteID();
        cbcName = cbc.getPaoName();
        twoWayCbc = DeviceTypesFuncs.isCBCTwoWay(cbc.getPaoType());
    }

    public final int getCcId() {
        return ccId;
    }

    public final String getCcName() {
        return ccName;
    }

    public final int getParentId() {
        return parentId;
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

    public final boolean isBankMoved() {
        return bankMoved;
    }

    @Override
    public int compareTo(ViewableCapBank o) {
        return ccName.compareToIgnoreCase(o.ccName);
    }
}