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


	public static final String STRING_CAPCONTROL_SUBBUS = "CCSUBBUS";
	public static final String STRING_CAPCONTROL_FEEDER = "CCFEEDER";
}
