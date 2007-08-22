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

	public static final String STRING_CAPCONTROL_SUBBUS = "CCSUBBUS";
	public static final String STRING_CAPCONTROL_FEEDER = "CCFEEDER";
    public static final String STRING_CAPCONTROL_AREA= "CCAREA";
    public static final String STRING_CAPCONTROL_SPECIAL_AREA= "CCSPECIALAREA";

}
