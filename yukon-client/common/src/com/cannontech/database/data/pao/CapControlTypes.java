package com.cannontech.database.data.pao;


/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 11:05:13 AM)
 * @author: 
 */
public interface CapControlTypes extends TypeBase
{   
    public final static int CAP_CONTROL_SUBBUS     = CAPCONTROL_OFFSET + 0;
	public final static int CAP_CONTROL_FEEDER     = CAPCONTROL_OFFSET + 1;
    public static final int CAP_CONTROL_AREA        = CAPCONTROL_OFFSET + 2;
    public static final int CAP_CONTROL_SPECIAL_AREA        = CAPCONTROL_OFFSET + 3;
    public final static int CAP_CONTROL_SUBSTATION     = CAPCONTROL_OFFSET + 4;
    public final static int CAP_CONTROL_STRATEGY     = CAPCONTROL_OFFSET + 6;
    public final static int CAP_CONTROL_CAPBANK = DeviceTypes.CAPBANK;
    public final static int CAP_CONTROL_LTC     = CAPCONTROL_OFFSET + 7;
    public final static int GANG_OPERATED_REGULATOR     = CAPCONTROL_OFFSET + 8;
    public final static int PHASE_OPERATED_REGULATOR     = CAPCONTROL_OFFSET + 9;
	
    public static final String STRING_CAPCONTROL_SUBSTATION = CapControlType.SUBSTATION.getDbValue();
	public static final String STRING_CAPCONTROL_SUBBUS = CapControlType.SUBBUS.getDbValue();
	public static final String STRING_CAPCONTROL_FEEDER = CapControlType.FEEDER.getDbValue();
    public static final String STRING_CAPCONTROL_AREA= CapControlType.AREA.getDbValue();
    public static final String STRING_CAPCONTROL_SPECIAL_AREA= CapControlType.SPECIAL_AREA.getDbValue();
    public static final String STRING_CAPCONTROL_STRATEGY = CapControlType.STRATEGY.getDbValue();
    public static final String STRING_CAPCONTROL_SCHEDULE = CapControlType.SCHEDULE.getDbValue();
    public static final String STRING_CAPCONTROL_LTC = CapControlType.LTC.getDbValue();
    public static final String STRING_GANG_OPERATED_REGULATOR = CapControlType.GO_REGULATOR.getDbValue();
    public static final String STRING_PHASE_OPERATED_REGULATOR = CapControlType.PO_REGULATOR.getDbValue();

}
