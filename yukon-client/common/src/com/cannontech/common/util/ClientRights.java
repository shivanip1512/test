package com.cannontech.common.util;

/**
 * Insert the type's description here.
 * Creation date: (3/18/2002 2:43:52 PM)
 * @author: 
 */
public interface ClientRights 
{
	public static final int CREATABLE			= 0x0000000C;
	public static final int ENABLEABLE			= 0x000000C0;
	public static final int STARTABLE			= 0x00000C00;


	/* hide clients by name */
	public static final int HIDE_MACS			= 0x00001000;
	public static final int HIDE_CAPCONTROL	= 0x00002000;
	public static final int HIDE_LOADCONTROL	= 0x00004000;
	public static final int HIDE_ALL				= 0x0000F000;
	
	/* Enable clients by name */
	public static final int ENABLE_SERVICES	= 0x00010000;

   //Enable some company specific paramenters   
   public static final int SHOW_SPECIAL_RIPPLE = 0x10000000;
   public static final int SHOW_ADDITIONAL_PROTOCOLS = 0x20000000;
   
   /* miscellaneous params */
   public static final int HIDE_ALARM_COLORS	= 0x80000000;
}
