package com.cannontech.database.data.point;

import com.cannontech.common.util.CtiUtilities;

/**
 * Class used for the logicalGroup fields inside points
 */
public final class PointLogicalGroups
{
	//all possible values for the logicalGroup field
	public static final int LGRP_DEFAULT = 0;
	public static final int LGRP_SOE = 1;


	
	//All the strings associated with points and the database
	public static final String[] LGRP_STRS = 
	{
		CtiUtilities.STRING_DEFAULT,
		"SOE"		
	};



	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 * @param typeEnum int
	 */
	public static String getLogicalGrp(int typeEnum) 
	{
		if( typeEnum < 0 || typeEnum > LGRP_STRS.length - 1 )		
			throw new IllegalArgumentException("PointLogicalGroups - received unknown type: " + typeEnum );
		else
			return LGRP_STRS[typeEnum];
	}

	
	/**
	 * This method was created in VisualAge.
	 * @return int
	 * @param typeStr java.lang.String
	 */
	public static int getLogicalGrp(String typeStr)
	{
		for( int i = 0; i < LGRP_STRS.length; i++ )
		{
			if( LGRP_STRS[i].equals(typeStr) )
				return i;
		}
	
		//Must not have found it
		throw new IllegalArgumentException("PointLogicalGroups - received unknown type: " + typeStr );
	}

	public static boolean isValidLogicalGroup( String str_ )
	{
		for( int i = 0; i < LGRP_STRS.length; i++ )
			if( LGRP_STRS[i].equals(str_) )
				return true;
				
		return false;
	}

}
