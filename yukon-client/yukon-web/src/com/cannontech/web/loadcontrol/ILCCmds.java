package com.cannontech.web.loadcontrol;

/**
 * @author rneuharth
 *
 * Defines the valid LoadControl command strings. 
 *
 */
public interface ILCCmds
{
	//Area Commands
	public static final String AREA_START_PROGS = "a_start_progs";
	public static final String AREA_STOP_PROGS = "a_stop_progs";
	public static final String AREA_TRIG_CHG = "a_trigger_chg";
	public static final String AREA_DAILY_CHG = "a_daily_chg";
	public static final String AREA_DISABLE = "a_disable";
    public static final String AREA_RESET_PEAK = "a_reset_peak";
	
	//Program Commands
	public static final String PROG_START = "p_start_prog";
	public static final String PROG_STOP = "p_stop_prog";
	public static final String PROG_DISABLE = "p_disable";
    public static final String PROG_CHANGE_GEAR = "p_change_gear";
	
	//Group Commands
	public static final String GRP_SHED = "g_shed";
	public static final String GRP_RESTORE = "g_restore";
	public static final String GRP_TRUE_CY = "g_truecyc";
	public static final String GRP_SMRT_CY = "g_smartcyc";
	public static final String GRP_DISABLE = "g_disable";
	public static final String GRP_CONFIRM = "g_confirm";

	//Scenario Commands
	public static final String SC_START = "sc_start";
	public static final String SC_STOP = "sc_stop";


	//the following commmands uses synchrounous messaging
	// Only LMProgramBase objects can do this
	public static final String[] SYNC_CMDS =
	{
		AREA_START_PROGS,
		PROG_START,
		SC_START,
        PROG_CHANGE_GEAR
	};

}
