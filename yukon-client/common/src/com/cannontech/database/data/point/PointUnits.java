 package com.cannontech.database.data.point;

/**
 * @author rneuharth
 * Jul 8, 2002 at 3:01:09 PM
 * 
 * A undefined generated comment
 */
public final class PointUnits
{
	//not used
	public static final int UOMID_INVALID = -1;

   //some predefined UOMID's that should be in all/most Databases!
   public static final int UOMID_KW = 0;
   public static final int UOMID_KWH = 1;
   public static final int UOMID_KVA = 2;
   public static final int UOMID_KVAR = 3;
   public static final int UOMID_KVAH = 4;
   public static final int UOMID_KVARH = 5;
   public static final int UOMID_KVOLTS = 6;
   public static final int UOMID_KQ = 7;
   public static final int UOMID_AMPS = 8;
   public static final int UOMID_COUNTS = 9;
   public static final int UOMID_DEGREES = 10;   
   public static final int UOMID_DOLLARS = 11; 
   public static final int UOMID_DOLLAR_CHAR = 12; 
   public static final int UOMID_FEET = 13; 
   public static final int UOMID_GALLONS = 14; 
   public static final int UOMID_GAL_PM = 15; 
   
   public static final int UOMID_GAS_CFT = 16; 
   public static final int UOMID_HOURS = 17; 
   public static final int UOMID_LEVELS = 18; 
   public static final int UOMID_MINUTES = 19; 
   public static final int UOMID_MW = 20; 
   public static final int UOMID_MWH = 21; 
   public static final int UOMID_MVA = 22; 
   public static final int UOMID_MVAR = 23; 
   public static final int UOMID_MVAH = 24;    
   public static final int UOMID_MVARH = 25;
   public static final int UOMID_OPS = 26;
   public static final int UOMID_PF = 27;
   public static final int UOMID_PERCENT = 28;
   public static final int UOMID_PERCENT_CHAR = 29;
   public static final int UOMID_PSI = 30;
   
   public static final int UOMID_SECONDS = 31;
   public static final int UOMID_TEMP_F = 32;
   public static final int UOMID_TEMP_C = 33;
   public static final int UOMID_VARS = 34;
   public static final int UOMID_VOLTS = 35;
   public static final int UOMID_VOLTAMPS = 36;
   public static final int UOMID_VA = 37;
   public static final int UOMID_WATR_CFT = 38;
   public static final int UOMID_WATTS = 39;
   public static final int UOMID_HZ = 40;
   public static final int UOMID_VOLTS_V2H = 41;
   public static final int UOMID_AMPS_V2H = 42;
   
	public static final int UOMID_TAP = 43;
	public static final int UOMID_MILES = 44;
	public static final int UOMID_MS = 45;
	public static final int UOMID_PPM = 46;
	public static final int UOMID_MPH = 47;
	public static final int UOMID_INCHES = 48;
	public static final int UOMID_KPH = 49;
	public static final int UOMID_MILIBARS = 50;
	public static final int UOMID_KH_H = 51;
	public static final int UOMID_M_S = 52;
	public static final int UOMID_KV = 53;
	public static final int UOMID_UNDEF = 54;
	public static final int UOMID_A = 55;


   public static final int[] CAP_CONTROL_VAR_UOMIDS =
   {
      UOMID_KVAR,
      UOMID_VARS,
      UOMID_MVAR,
      UOMID_KQ,      
   };


   public static final int[] CAP_CONTROL_WATTS_UOMIDS =
   {
      UOMID_KW,
      UOMID_MW
   };
   
	/**
	 * Constructor for PointUnit.
	 */
	private PointUnits()
	{
		super();
	}

}
