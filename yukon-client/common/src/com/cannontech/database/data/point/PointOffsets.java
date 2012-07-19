package com.cannontech.database.data.point;

public class PointOffsets 
{
   public static final int PT_OFFSET_TOTAL_KWH				= 1;
   
   public static final int PT_OFFSET_KW_DEMAND				= 1;
   public static final int PT_OFFSET_VOLTAGE_DEMAND			= 4;
   public static final int PT_OFFSET_PEAK_KW_DEMAND			= 11;
   public static final int PT_OFFSET_MAX_VOLT_DEMAND 		= 14;
   public static final int PT_OFFSET_MIN_VOLT_DEMAND 		= 15;
   public static final int PT_OFFSET_LP_KW_DEMAND			= 15;
   public static final int PT_OFFSET_BLINK_COUNT			= 20;
   public static final int PT_OFFSET_FROZEN_PEAK_DEMAND		= 21;
   public static final int PT_OFFSET_TOTAL_KVARH			= 21;
   public static final int PT_OFFSET_FROZEN_MAX_VOLT		= 24;
   public static final int PT_OFFSET_FROZEN_MIN_VOLT		= 25;
   public static final int PT_OFFSET_KVAR_DEMAND			= 35;
	
   public static final int PT_OFFSET_OUTAGE                 = 100;
   public static final int PT_OFFSET_LPROFILE_KW_DEMAND     = 101;
   public static final int PT_OFFSET_PROFILE_CHANNEL2       = 102; 
   public static final int PT_OFFSET_PROFILE_CHANNEL3       = 103; 
   
   public static final int PT_OFFSET_LPROFILE_VOLTAGE_DEMAND = 104;
   
   public static final int PT_OFFSET_DISABLE_STATUS          = 500;
   
   public static final int PT_OFFSET_TRANS_STATUS			= 2000;

   public static final int PT_OFFSET_DAILY_HISTORY			= 2500;
   public static final int PT_OFFSET_MONTHLY_HISTORY		= 2501;
   public static final int PT_OFFSET_SEASONAL_HISTORY		= 2502;
   public static final int PT_OFFSET_ANNUAL_HISTORY		    = 2503;
   
   public static final int PT_OFFSET_CONTROL_COUNTDOWN		= 2505;
   
   public static final int PT_OFFSET_BILLING_BASELINE		= 6000;	//Billing Point, curtailment settlement (CSVBilling)
}
