package com.cannontech.cbc.data;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public final class CapControlTags 
{
	
	public static final int TEMPORARY_MOVED			= 0x00000001;  
	public static final int TEMPORARY_MOVED_ORIG		= 0x00000002;

	public static boolean isTemporaryMove( int value )
	{
		return (value & TEMPORARY_MOVED) != 0;
	}

	public static boolean isTemporaryMoveOrig( int value )
	{
		return (value & TEMPORARY_MOVED_ORIG) != 0;
	}

}
