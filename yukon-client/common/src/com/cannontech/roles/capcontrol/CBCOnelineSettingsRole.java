package com.cannontech.roles.capcontrol;

import com.cannontech.roles.CapControlRoleDefs;

public interface CBCOnelineSettingsRole {

    //SUBSTATION
    public static final int SUB_ONELINE_PROPID_BASE = CapControlRoleDefs.CBC_ONELINE_PROPERTYID_BASE;
    public static final int SUB_ROLEID = CapControlRoleDefs.CBC_ONELINE_ROLEID_BASE;
    public static final int SUB_TARGET = SUB_ONELINE_PROPID_BASE;
    public static final int SUB_VARLOAD = SUB_ONELINE_PROPID_BASE - 1;
    public static final int SUB_POWER_FACTOR = SUB_ONELINE_PROPID_BASE - 2;
    public static final int SUB_WATTSVOLTS = SUB_ONELINE_PROPID_BASE - 3;
    public static final int SUB_OP_CNT = SUB_ONELINE_PROPID_BASE - 4;

    //FEEDER
    public static final int FDR_ONELINE_PROPID_BASE = SUB_ONELINE_PROPID_BASE - 100;
    public static final int FDR_ROLEID = SUB_ROLEID - 1;
    public static final int FDR_KVAR = FDR_ONELINE_PROPID_BASE;
    public static final int FDR_PF = FDR_ONELINE_PROPID_BASE - 1;
    public static final int FDR_WATTVOLT = FDR_ONELINE_PROPID_BASE - 2;
    public static final int FDR_OP_CNT = FDR_ONELINE_PROPID_BASE - 3;
    
    //CAP BANK
    public static final int CAP_ONELINE_PROPID_BASE = SUB_ONELINE_PROPID_BASE - 200;
    public static final int CAP_ROLEID = SUB_ROLEID - 2;
    public static final int CAP_OPCNT = CAP_ONELINE_PROPID_BASE;
    public static final int CAP_BANK_SIZE = CAP_ONELINE_PROPID_BASE - 1;

}
