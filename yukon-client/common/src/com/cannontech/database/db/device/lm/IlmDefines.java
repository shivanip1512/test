package com.cannontech.database.db.device.lm;

import com.cannontech.common.util.CtiUtilities;

/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 10:45:14 AM)
 * @author: 
 */
public interface IlmDefines 
{
	// --------------------------------------------------------------- 
	//    Start LM Gears Defines
	// --------------------------------------------------------------- 
	/** NOTE ON GEARS ***
	 * Gears numbers start at 1, not 0 (zero).
	 * The gear number is also stored like this in the DB.
	 */	
	//A large limit is good
	public static final int MAX_GEAR_COUNT = 16;
	
	public static final int INVALID_GEAR_NUM = -1;
	
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
	public static final String CONTROL_TRUE_CYCLE = "TrueCycle";
	public static final String THERMOSTAT_SETBACK = "ThermostatSetback";
	public static final String THERMOSTAT_PRE_OPERATE = "ThermostatPre-operate";
	public static final String NO_CONTROL = "NoControl";

	//a mapping of all the possible control methods
	public static final String[] ALL_CONTROL_METHODS =
	{
		CONTROL_TIME_REFRESH,
		CONTROL_SMART_CYCLE,
		CONTROL_TRUE_CYCLE,
		CONTROL_MASTER_CYCLE,
		CONTROL_ROTATION,
		CONTROL_LATCHING,
		THERMOSTAT_PRE_OPERATE,
		THERMOSTAT_SETBACK,
		NO_CONTROL
		
	};

	//all possible method stop type values
	public static final String STOP_RESTORE = "Restore";
	public static final String STOP_TIME_IN = "TimeIn";
	public static final String STOP_STOP_CYCLE = "StopCycle";
	public static final String STOP_RAMP_OUT = "RampOut";


	//all possible change conditions
	public static final String CHANGE_NONE = "None"; //Manually Only
	public static final String CHANGE_DURATION = "Duration"; //After a Duration
	public static final String CHANGE_PRIORITY = "Priority"; //Priority Change
	public static final String CHANGE_TRIGGER_OFFSET = "TriggerOffset"; //Above Trigger

	//all possible values for the MethodOptionType in DirectGears
	public static final String OPTION_FIXED_COUNT = "FixedCount";
	public static final String OPTION_COUNT_DOWN = "CountDown";
	public static final String OPTION_LIMITED_COUNT_DOWN = "LimitedCountDown";



	// --------------------------------------------------------------- 
	//    Start LM Trigger Defines
	// --------------------------------------------------------------- 
	public static final String TYPE_THRESHOLD = "Threshold";

	public static final String TYPE_STATUS = "Status";

	public static final int INVALID_INT_VALUE = 0;

	/* Projection specific defines */
	public static final String PROJ_TYPE_NONE		= CtiUtilities.STRING_NONE;
	public static final String PROJ_TYPE_LSF		= "LSF";	



	// --------------------------------------------------------------- 
	//    Start LM Group Defines
	// ---------------------------------------------------------------
	public static final Integer NONE_ADDRESS_ID			= new Integer(0);
	public static final String TYPE_SERVICE				= "SERVICE";
	public static final String TYPE_GEO						= "GEO";
	public static final String TYPE_SUBSTATION			= "SUBSTATION";
	public static final String TYPE_FEEDER					= "FEEDER";
	public static final String TYPE_PROGRAM				= "PROGRAM";
	
	
	public static final String LEVEL_BRONZE				= "Bronze";
	public static final String LEVEL_LEAD					= "Lead";
	public static final String LEVEL_MCT					= "MCT Address";

	public static final String SA_LOAD_1 = "Load 1";
	public static final String SA_LOAD_2 = "Load 2";
	public static final String SA_LOAD_3 = "Load 3";
	public static final String SA_LOAD_4 = "Load 4";
	public static final String SA_LOAD_12 = "Load 1,2";
	public static final String SA_LOAD_123 = "Load 1,2,3";
	public static final String SA_LOAD_1234 = "Load 1,2,3,4";
	public static final String SA_LOAD_TEST = "Test";
}