package com.cannontech.common.constants;

/**
 * @author rneuharth
 *
 * A mapping interface that contains the constants found in the
 * Yukon database.
 */
public interface YukonListEntryTypes 
{
	//standard EntryIDs that should be in every DB
	public static final int YUK_ENTRY_ID_EMAIL = 1;
	public static final int YUK_ENTRY_ID_PHONE = 2;
	public static final int YUK_ENTRY_ID_PAGER = 3;
	
	public static final int YUK_ENTRY_ID_FAX = 4;
	public static final int YUK_ENTRY_ID_HOME_PHONE = 5;
	public static final int YUK_ENTRY_ID_WORK_PHONE = 6;

	
	//YukonDefinitionIDs used by STARS 
	public static final int YUK_DEF_ID_CUST_EVENT_LMPROGRAM			= 1001;
	public static final int YUK_DEF_ID_CUST_EVENT_LMHARDWARE		= 1002;
	public static final int YUK_DEF_ID_CUST_EVENT_LMTHERMOSTAT_MANUAL	= 1003;
	public static final int YUK_DEF_ID_CUST_ACT_SIGNUP				= 1101;
	public static final int YUK_DEF_ID_CUST_ACT_PENDING				= 1102;
	public static final int YUK_DEF_ID_CUST_ACT_COMPLETED			= 1103;
	public static final int YUK_DEF_ID_CUST_ACT_TERMINATION			= 1104;
	public static final int YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION	= 1105;
	public static final int YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION	= 1106;
	public static final int YUK_DEF_ID_CUST_ACT_INSTALL				= 1107;
	public static final int YUK_DEF_ID_CUST_ACT_CONFIG				= 1108;
	public static final int YUK_DEF_ID_CUST_ACT_PROGRAMMING			= 1109;
	public static final int YUK_DEF_ID_CUST_ACT_MANUAL_OPTION		= 1110;
	public static final int YUK_DEF_ID_CUST_ACT_UNINSTALL			= 1111;
	public static final int YUK_DEF_ID_INV_CAT_ONEWAYREC			= 1201;
	public static final int YUK_DEF_ID_INV_CAT_TWOWAYREC			= 1202;
	public static final int YUK_DEF_ID_INV_CAT_MCT					= 1203;
	public static final int YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT			= 1301;
	public static final int YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM		= 1302;
	public static final int YUK_DEF_ID_DEV_TYPE_MCT					= 1303;
	public static final int YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT	= 1304;
	public static final int YUK_DEF_ID_DEV_TYPE_LCR_4000			= 1305;
	public static final int YUK_DEF_ID_DEV_TYPE_LCR_3000			= 1306;
	public static final int YUK_DEF_ID_DEV_TYPE_LCR_2000			= 1307;
	public static final int YUK_DEF_ID_DEV_TYPE_LCR_1000			= 1308;
	public static final int YUK_DEF_ID_DEV_TYPE_SA205				= 1309;
	public static final int YUK_DEF_ID_DEV_TYPE_SA305				= 1310;
	public static final int YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM		= 1311;
	public static final int YUK_DEF_ID_DEV_TYPE_ENERGYPRO			= 3100;
	public static final int YUK_DEF_ID_APP_CAT_DEFAULT				= 1400;
	public static final int YUK_DEF_ID_APP_CAT_AIR_CONDITIONER		= 1401;
	public static final int YUK_DEF_ID_APP_CAT_WATER_HEATER			= 1402;
	public static final int YUK_DEF_ID_APP_CAT_STORAGE_HEAT			= 1403;
	public static final int YUK_DEF_ID_APP_CAT_HEAT_PUMP			= 1404;
	public static final int YUK_DEF_ID_APP_CAT_DUAL_FUEL			= 1405;
	public static final int YUK_DEF_ID_APP_CAT_GENERATOR			= 1406;
	public static final int YUK_DEF_ID_APP_CAT_GRAIN_DRYER			= 1407;
	public static final int YUK_DEF_ID_APP_CAT_IRRIGATION			= 1408;
	public static final int YUK_DEF_ID_SERV_STAT_PENDING			= 1501;
	public static final int YUK_DEF_ID_SERV_STAT_SCHEDULED			= 1502;
	public static final int YUK_DEF_ID_SERV_STAT_COMPLETED			= 1503;
	public static final int YUK_DEF_ID_SERV_STAT_CANCELLED			= 1504;
	public static final int YUK_DEF_ID_SEARCH_TYPE_ACCT_NO			= 1601;
	public static final int YUK_DEF_ID_SEARCH_TYPE_PHONE_NO			= 1602;
	public static final int YUK_DEF_ID_SEARCH_TYPE_LAST_NAME		= 1603;
	public static final int YUK_DEF_ID_SEARCH_TYPE_SERIAL_NO		= 1604;
	public static final int YUK_DEF_ID_SEARCH_TYPE_MAP_NO			= 1605;
	public static final int YUK_DEF_ID_SEARCH_TYPE_ADDRESS			= 1606;
	public static final int YUK_DEF_ID_SEARCH_TYPE_ALT_TRACK_NO		= 1607;
	public static final int YUK_DEF_ID_DEV_STAT_AVAIL				= 1701;
	public static final int YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL		= 1702;
	public static final int YUK_DEF_ID_DEV_STAT_UNAVAIL				= 1703;
	public static final int YUK_DEF_ID_MANU_UNKNOWN					= 1801;
	public static final int YUK_DEF_ID_LOC_UNKNOWN					= 1901;
	public static final int YUK_DEF_ID_TOW_WEEKDAY					= 2101;
	public static final int YUK_DEF_ID_TOW_WEEKEND					= 2102;
	public static final int YUK_DEF_ID_TOW_SATURDAY					= 2103;
	public static final int YUK_DEF_ID_TOW_SUNDAY					= 2104;
	public static final int YUK_DEF_ID_TOW_MONDAY					= 2105;
	public static final int YUK_DEF_ID_TOW_TUESDAY					= 2106;
	public static final int YUK_DEF_ID_TOW_WEDNESDAY				= 2107;
	public static final int YUK_DEF_ID_TOW_THURSDAY					= 2108;
	public static final int YUK_DEF_ID_TOW_FRIDAY					= 2109;
	public static final int YUK_DEF_ID_QUE_TYPE_SIGNUP				= 2201;
	public static final int YUK_DEF_ID_QUE_TYPE_EXIT				= 2202;
	public static final int YUK_DEF_ID_ANS_TYPE_SELECTION			= 2301;
	public static final int YUK_DEF_ID_ANS_TYPE_FREE_FORM			= 2302;
	public static final int YUK_DEF_ID_THERM_MODE_DEFAULT			= 2401;
	public static final int YUK_DEF_ID_THERM_MODE_COOL				= 2402;
	public static final int YUK_DEF_ID_THERM_MODE_HEAT				= 2403;
	public static final int YUK_DEF_ID_THERM_MODE_OFF				= 2404;
	public static final int YUK_DEF_ID_THERM_MODE_AUTO				= 2405;
	public static final int YUK_DEF_ID_THERM_MODE_EMERGENCY_HEAT	= 2406;
	public static final int YUK_DEF_ID_FAN_STAT_DEFAULT				= 2501;
	public static final int YUK_DEF_ID_FAN_STAT_AUTO				= 2502;
	public static final int YUK_DEF_ID_FAN_STAT_ON					= 2503;
	public static final int YUK_DEF_ID_OPTOUT_PERIOD_TOMORROW		= 2601;
	public static final int YUK_DEF_ID_OPTOUT_PERIOD_TODAY			= 2602;
	public static final int YUK_DEF_ID_OPTOUT_PERIOD_REPEAT_LAST	= 2699;
	public static final int YUK_DEF_ID_INV_SEARCH_BY_SERIAL_NO		= 2701;
	public static final int YUK_DEF_ID_INV_SEARCH_BY_ACCT_NO		= 2702;
	public static final int YUK_DEF_ID_INV_SEARCH_BY_PHONE_NO		= 2703;
	public static final int YUK_DEF_ID_INV_SEARCH_BY_LAST_NAME		= 2704;
	public static final int YUK_DEF_ID_INV_SEARCH_BY_ORDER_NO		= 2705;
	public static final int YUK_DEF_ID_INV_SEARCH_BY_ADDRESS		= 2706;
	public static final int YUK_DEF_ID_INV_SEARCH_BY_ALT_TRACK_NO	= 2707;
	public static final int YUK_DEF_ID_INV_SORT_BY_SERIAL_NO		= 2801;
	public static final int YUK_DEF_ID_INV_SORT_BY_INST_DATE		= 2802;
	public static final int YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE		= 2901;
	public static final int YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY	= 2902;
	public static final int YUK_DEF_ID_INV_FILTER_BY_LOCATION		= 2903;
	public static final int YUK_DEF_ID_INV_FILTER_BY_CONFIG			= 2904;
	public static final int YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS		= 2905;
	
	public static final int YUK_DEF_ID_GED_TIMESTAMP				= 3201;
	public static final int YUK_DEF_ID_GED_SETPOINTS				= 3202;
	public static final int YUK_DEF_ID_GED_FAN_SWITCH				= 3203;
	public static final int YUK_DEF_ID_GED_SYSTEM_SWITCH			= 3204;
	public static final int YUK_DEF_ID_GED_DISPLAYED_TEMP			= 3205;
	public static final int YUK_DEF_ID_GED_SCHEDULE_MON_WAKE		= 3206;
	public static final int YUK_DEF_ID_GED_SCHEDULE_MON_LEAVE		= 3207;
	public static final int YUK_DEF_ID_GED_SCHEDULE_MON_RETURN		= 3208;
	public static final int YUK_DEF_ID_GED_SCHEDULE_MON_SLEEP		= 3209;
	public static final int YUK_DEF_ID_GED_SCHEDULE_TUE_WAKE		= 3210;
	public static final int YUK_DEF_ID_GED_SCHEDULE_TUE_LEAVE		= 3211;
	public static final int YUK_DEF_ID_GED_SCHEDULE_TUE_RETURN		= 3212;
	public static final int YUK_DEF_ID_GED_SCHEDULE_TUE_SLEEP		= 3213;
	public static final int YUK_DEF_ID_GED_SCHEDULE_WED_WAKE		= 3214;
	public static final int YUK_DEF_ID_GED_SCHEDULE_WED_LEAVE		= 3215;
	public static final int YUK_DEF_ID_GED_SCHEDULE_WED_RETURN		= 3216;
	public static final int YUK_DEF_ID_GED_SCHEDULE_WED_SLEEP		= 3217;
	public static final int YUK_DEF_ID_GED_SCHEDULE_THU_WAKE		= 3218;
	public static final int YUK_DEF_ID_GED_SCHEDULE_THU_LEAVE		= 3219;
	public static final int YUK_DEF_ID_GED_SCHEDULE_THU_RETURN		= 3220;
	public static final int YUK_DEF_ID_GED_SCHEDULE_THU_SLEEP		= 3221;
	public static final int YUK_DEF_ID_GED_SCHEDULE_FRI_WAKE		= 3222;
	public static final int YUK_DEF_ID_GED_SCHEDULE_FRI_LEAVE		= 3223;
	public static final int YUK_DEF_ID_GED_SCHEDULE_FRI_RETURN		= 3224;
	public static final int YUK_DEF_ID_GED_SCHEDULE_FRI_SLEEP		= 3225;
	public static final int YUK_DEF_ID_GED_SCHEDULE_SAT_WAKE		= 3226;
	public static final int YUK_DEF_ID_GED_SCHEDULE_SAT_LEAVE		= 3227;
	public static final int YUK_DEF_ID_GED_SCHEDULE_SAT_RETURN		= 3228;
	public static final int YUK_DEF_ID_GED_SCHEDULE_SAT_SLEEP		= 3229;
	public static final int YUK_DEF_ID_GED_SCHEDULE_SUN_WAKE		= 3230;
	public static final int YUK_DEF_ID_GED_SCHEDULE_SUN_LEAVE		= 3231;
	public static final int YUK_DEF_ID_GED_SCHEDULE_SUN_RETURN		= 3232;
	public static final int YUK_DEF_ID_GED_SCHEDULE_SUN_SLEEP		= 3233;
	public static final int YUK_DEF_ID_GED_OUTDOOR_TEMP				= 3234;
	public static final int YUK_DEF_ID_GED_DLC						= 3235;
	public static final int YUK_DEF_ID_GED_FILTER					= 3236;
	public static final int YUK_DEF_ID_GED_SETPOINT_LIMITS			= 3237;
	public static final int YUK_DEF_ID_GED_RUNTIMES					= 3238;
	public static final int YUK_DEF_ID_GED_BATTERY					= 3239;
	public static final int YUK_DEF_ID_GED_UTILITY_SETPOINTS		= 3240;
	public static final int YUK_DEF_ID_GED_STRING					= 3299;
	
	public static final int YUK_DEF_ID_SO_SEARCH_BY_ORDER_NO		= 3301;
	public static final int YUK_DEF_ID_SO_SEARCH_BY_ACCT_NO			= 3302;
	public static final int YUK_DEF_ID_SO_SEARCH_BY_PHONE_NO		= 3303;
	public static final int YUK_DEF_ID_SO_SEARCH_BY_LAST_NAME		= 3304;
	public static final int YUK_DEF_ID_SO_SEARCH_BY_SERIAL_NO		= 3305;
	public static final int YUK_DEF_ID_SO_SEARCH_BY_ADDRESS			= 3306;
	public static final int YUK_DEF_ID_SO_SORT_BY_ORDER_NO			= 3401;
	public static final int YUK_DEF_ID_SO_SORT_BY_DATE_TIME			= 3402;
	public static final int YUK_DEF_ID_SO_FILTER_BY_STATUS			= 3501;
	public static final int YUK_DEF_ID_SO_FILTER_BY_SRV_TYPE		= 3502;
	public static final int YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY		= 3503;

}
