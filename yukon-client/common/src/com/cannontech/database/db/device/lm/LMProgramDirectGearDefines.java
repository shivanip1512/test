package com.cannontech.database.db.device.lm;

/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 10:45:14 AM)
 * @author: 
 */
public interface LMProgramDirectGearDefines 
{
	//all possible values for groupSelectionMethod
	public static final String SELECTION_LAST_CONTROLLED = "LastControlled";
	public static final String SELECTION_ALWAYS_FIRST_GROUP = "AlwaysFirstGroup";
	public static final String SELECTION_LEAST_CONTROL_TIME = "LeastControlTime";

	//all possible control methods for a gear
	public static final String CONTROL_TIME_REFRESH = "TimeRefresh";
	public static final String CONTROL_SMART_CYCLE = "SmartCycle";
	public static final String CONTROL_MASTER_CYCLE = "MasterCycle";
	public static final String CONTROL_ROTATION = "Rotation";
	public static final String CONTROL_LATCHING = "Latching";

	//a mapping of all the possible control methods
	public static final String[] ALL_CONTROL_METHODS =
	{
		CONTROL_TIME_REFRESH,
		CONTROL_SMART_CYCLE,
		CONTROL_MASTER_CYCLE,
		CONTROL_ROTATION,
		CONTROL_LATCHING
	};

	//all possible method stop type values
	public static final String STOP_RESTORE = "Restore";
	public static final String STOP_TIME_IN = "TimeIn";
	public static final String STOP_STOP_CYCLE = "StopCycle";


	//all possible change conditions
	public static final String CHANGE_NONE = "None"; //Manually Only
	public static final String CHANGE_DURATION = "Duration"; //After a Duration
	public static final String CHANGE_PRIORITY = "Priority"; //Priority Change
	public static final String CHANGE_TRIGGER_OFFSET = "TriggerOffset"; //Above Trigger

	//all possible values for the MethodOptionType in DirectGears
	public static final String OPTION_FIXED_COUNT = "FixedCount";
	public static final String OPTION_COUNT_DOWN = "CountDown";
	public static final String OPTION_LIMITED_COUNT_DOWN = "LimitedCountDown";
	
}
