package com.cannontech.database.data.point;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface IPointOffsets 
{

   public static final int PT_OFFSET_TOTAL_KWH				= 1;
   public static final int PT_OFFSET_TOTAL_KVARH			= 21;
   public static final int PT_OFFSET_LP_KW_DEMAND			= 15;
   public static final int PT_OFFSET_KVAR_DEMAND			= 35;
	
	
   public static final int PT_OFFSET_LPROFILE_KW_DEMAND   = 101;

	
	public static final int PT_OFFSET_TRANS_STATUS			= 2000;
	
   public static final int PT_OFFSET_DAILY_HISTORY			= 2500;
   public static final int PT_OFFSET_MONTHLY_HISTORY		= 2501;
   public static final int PT_OFFSET_SEASONAL_HISTORY		= 2502;
   public static final int PT_OFFSET_ANNUAL_HISTORY		= 2503;


}
