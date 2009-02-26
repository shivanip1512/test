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
    public final static int CAP_CONTROL_CAPBANK = DeviceTypes.CAPBANK;
	
    public static final String STRING_CAPCONTROL_SUBSTATION = CapControlType.SUBSTATION.getDisplayValue();
	public static final String STRING_CAPCONTROL_SUBBUS = CapControlType.SUBBUS.getDisplayValue();
	public static final String STRING_CAPCONTROL_FEEDER = CapControlType.FEEDER.getDisplayValue();
    public static final String STRING_CAPCONTROL_AREA= CapControlType.AREA.getDisplayValue();
    public static final String STRING_CAPCONTROL_SPECIAL_AREA= CapControlType.SPECIAL_AREA.getDisplayValue();
    public static final String STRING_CAPCONTROL_CAPBANK = DeviceTypes.STRING_CAP_BANK[0];

}
