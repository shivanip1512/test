package com.cannontech.roles.capcontrol;

import com.cannontech.roles.CapControlRoleDefs;

public interface CBCOnelineSettingsRole {

    //SUBSTATION
    public static final int SUB_ONELINE_PROPID_BASE = CapControlRoleDefs.CBC_ONELINE_PROPERTYID_BASE;
    public static final int SUB_ROLEID = CapControlRoleDefs.CBC_ONELINE_ROLEID_BASE;
    public static final int SUB_TARGET = SUB_ONELINE_PROPID_BASE;
    public static final int SUB_VARLOAD = SUB_ONELINE_PROPID_BASE - 1;
    public static final int SUB_EST_VARLOAD = SUB_ONELINE_PROPID_BASE - 2;
    public static final int SUB_POWER_FACTOR = SUB_ONELINE_PROPID_BASE - 3;
    public static final int SUB_EST_POWER_FACTOR = SUB_ONELINE_PROPID_BASE - 4;
    public static final int SUB_WATTS = SUB_ONELINE_PROPID_BASE - 5;
    public static final int SUB_VOLTS = SUB_ONELINE_PROPID_BASE - 6;
    public static final int SUB_DAILY_OPCNT = SUB_ONELINE_PROPID_BASE - 7;
    public static final int SUB_MAX_DAILY_OPCNT = SUB_ONELINE_PROPID_BASE - 8;
    public static final int SUB_AREA = SUB_ONELINE_PROPID_BASE - 9;
    public static final int SUB_CTL_METHOD = SUB_ONELINE_PROPID_BASE - 10;
       
    
    //FEEDER
    public static final int FDR_ONELINE_PROPID_BASE = SUB_ONELINE_PROPID_BASE - 100;
    public static final int FDR_ROLEID = SUB_ROLEID - 1;
    public static final int FDR_KVAR = FDR_ONELINE_PROPID_BASE;
    public static final int FDR_PF = FDR_ONELINE_PROPID_BASE - 1;
    public static final int FDR_WATT = FDR_ONELINE_PROPID_BASE - 2;
    public static final int FDR_OP_CNT = FDR_ONELINE_PROPID_BASE - 3;
    public static final int FDR_VOLT = FDR_ONELINE_PROPID_BASE - 4;
    
    
    
    //CAP BANK
    public static final int CAP_ONELINE_PROPID_BASE = SUB_ONELINE_PROPID_BASE - 200;
    public static final int CAP_ROLEID = SUB_ROLEID - 2;
    public static final int CAP_OPCNT = CAP_ONELINE_PROPID_BASE;
    public static final int CAP_BANK_SIZE = CAP_ONELINE_PROPID_BASE - 1;
    public static final int CAP_CBC_NAME = CAP_ONELINE_PROPID_BASE - 2;

}
