package com.cannontech.database.data.pao;

public interface CapControlTypes extends TypeBase
{   
    public final static int CAP_CONTROL_SUBBUS     = CAPCONTROL_OFFSET + 0;
	public final static int CAP_CONTROL_FEEDER     = CAPCONTROL_OFFSET + 1;
    public static final int CAP_CONTROL_AREA        = CAPCONTROL_OFFSET + 2;
    public static final int CAP_CONTROL_SPECIAL_AREA        = CAPCONTROL_OFFSET + 3;
    public final static int CAP_CONTROL_SUBSTATION     = CAPCONTROL_OFFSET + 4;
    public final static int CAP_CONTROL_STRATEGY     = CAPCONTROL_OFFSET + 6;
    public final static int CAP_CONTROL_LTC     = CAPCONTROL_OFFSET + 7;
    public final static int GANG_OPERATED_REGULATOR     = CAPCONTROL_OFFSET + 8;
    public final static int PHASE_OPERATED_REGULATOR     = CAPCONTROL_OFFSET + 9;
}
