package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.stars.util.ECUtils;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsThermostatSettings extends LiteBase {
	
	private static final java.text.SimpleDateFormat dateFormat =
			new java.text.SimpleDateFormat( "yyyy/MM/dd HH:mm:ss z" );
	private static final String UNKNOWN_STRING = "(UNKNOWN)";
	
	private LiteLMThermostatSchedule thermostatSchedule = null;
	private ArrayList thermostatManualEvents = null;	// List of LMThermostatManualEvent
	private LiteStarsGatewayEndDevice dynamicData = null;
	
	public LiteStarsThermostatSettings() {
		super();
	}
	
	public LiteStarsThermostatSettings(int inventoryID) {
		super();
		setInventoryID( inventoryID );
	}
	
	public int getInventoryID() {
		return getLiteID();
	}
	
	public void setInventoryID(int inventoryID) {
		setLiteID( inventoryID );
	}
	
	public void updateThermostatSettings(LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany) {
		int hwTypeDefID = YukonListFuncs.getYukonListEntry(liteHw.getLmHardwareTypeID()).getYukonDefID();
		
		Object[][] data = com.cannontech.database.db.stars.hardware.GatewayEndDevice.getHardwareData(
				liteHw.getManufacturerSerialNumber(), new Integer(hwTypeDefID) );
		if (data == null || data.length == 0) return;
		
		/* Thermostat schedules
		 * First dimension: monday, tuesday, ..., sunday
		 * Second dimension: wake, leave, return, sleep
		 * Third dimension: hour, minute, cool setpoint, heat setpoint
		 */
		int[][][] schedules = new int[7][4][4];
		
		ArrayList infoStrings = getDynamicData().getInfoStrings();
		infoStrings.clear();
		
		int fanStateDftID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT ).getEntryID();
		int fanStateAutoID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_AUTO ).getEntryID();
		int fanStateOnID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_ON ).getEntryID();
		int thermModeDftID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT ).getEntryID();
		int thermModeCoolID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_COOL ).getEntryID();
		int thermModeHeatID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_HEAT ).getEntryID();
		int thermModeOffID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_OFF ).getEntryID();
		int thermModeAutoID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_AUTO ).getEntryID();
		
		for (int i = 0; i < data.length; i++) {
			try {
				int dataType = ((Integer) data[i][0]).intValue();
				String dataValue = (String) data[i][1];
				if (dataValue.equalsIgnoreCase(UNKNOWN_STRING)) continue;
				
				if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_TIMESTAMP) {
					dynamicData.setTimestamp( dateFormat.parse(dataValue).getTime() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SETPOINTS) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					dynamicData.setCoolSetpoint( Integer.parseInt(st.nextToken()) );
					dynamicData.setHeatSetpoint( Integer.parseInt(st.nextToken()) );
					dynamicData.setSetpointStatus( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_FAN_SWITCH) {
					int fanState = fanStateDftID;
					if (dataValue.equalsIgnoreCase("AUTO"))
						fanState = fanStateAutoID;
					else if (dataValue.equalsIgnoreCase("ON"))
						fanState = fanStateOnID;
					dynamicData.setFanSwitch( fanState );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SYSTEM_SWITCH) {
					StringTokenizer st = new StringTokenizer(dataValue, ",");
					String mode = st.nextToken();
					int thermMode = thermModeDftID;
					if (mode.equalsIgnoreCase("COOL"))
						thermMode = thermModeCoolID;
					else if (mode.equalsIgnoreCase("HEAT"))
						thermMode = thermModeHeatID;
					else if (mode.equalsIgnoreCase("OFF"))
						thermMode = thermModeOffID;
					dynamicData.setLastSystemSwitch( thermMode );
					
					if (st.hasMoreTokens() && st.nextToken().equalsIgnoreCase("(AUTO)"))
						dynamicData.setSystemSwitch( thermModeAutoID );
					else
						dynamicData.setSystemSwitch( thermMode );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_DISPLAYED_TEMP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					dynamicData.setDisplayedTemperature( Integer.parseInt(st.nextToken()) );
					dynamicData.setDisplayedTempUnit( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_MON_WAKE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[0][0][0] = Integer.parseInt( st.nextToken() );
					schedules[0][0][1] = Integer.parseInt( st.nextToken() );
					schedules[0][0][2] = Integer.parseInt( st.nextToken() );
					schedules[0][0][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_MON_LEAVE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[0][1][0] = Integer.parseInt( st.nextToken() );
					schedules[0][1][1] = Integer.parseInt( st.nextToken() );
					schedules[0][1][2] = Integer.parseInt( st.nextToken() );
					schedules[0][1][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_MON_RETURN) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[0][2][0] = Integer.parseInt( st.nextToken() );
					schedules[0][2][1] = Integer.parseInt( st.nextToken() );
					schedules[0][2][2] = Integer.parseInt( st.nextToken() );
					schedules[0][2][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_MON_SLEEP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[0][3][0] = Integer.parseInt( st.nextToken() );
					schedules[0][3][1] = Integer.parseInt( st.nextToken() );
					schedules[0][3][2] = Integer.parseInt( st.nextToken() );
					schedules[0][3][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_TUE_WAKE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[1][0][0] = Integer.parseInt( st.nextToken() );
					schedules[1][0][1] = Integer.parseInt( st.nextToken() );
					schedules[1][0][2] = Integer.parseInt( st.nextToken() );
					schedules[1][0][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_TUE_LEAVE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[1][1][0] = Integer.parseInt( st.nextToken() );
					schedules[1][1][1] = Integer.parseInt( st.nextToken() );
					schedules[1][1][2] = Integer.parseInt( st.nextToken() );
					schedules[1][1][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_TUE_RETURN) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[1][2][0] = Integer.parseInt( st.nextToken() );
					schedules[1][2][1] = Integer.parseInt( st.nextToken() );
					schedules[1][2][2] = Integer.parseInt( st.nextToken() );
					schedules[1][2][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_TUE_SLEEP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[1][3][0] = Integer.parseInt( st.nextToken() );
					schedules[1][3][1] = Integer.parseInt( st.nextToken() );
					schedules[1][3][2] = Integer.parseInt( st.nextToken() );
					schedules[1][3][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_WED_WAKE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[2][0][0] = Integer.parseInt( st.nextToken() );
					schedules[2][0][1] = Integer.parseInt( st.nextToken() );
					schedules[2][0][2] = Integer.parseInt( st.nextToken() );
					schedules[2][0][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_WED_LEAVE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[2][1][0] = Integer.parseInt( st.nextToken() );
					schedules[2][1][1] = Integer.parseInt( st.nextToken() );
					schedules[2][1][2] = Integer.parseInt( st.nextToken() );
					schedules[2][1][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_WED_RETURN) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[2][2][0] = Integer.parseInt( st.nextToken() );
					schedules[2][2][1] = Integer.parseInt( st.nextToken() );
					schedules[2][2][2] = Integer.parseInt( st.nextToken() );
					schedules[2][2][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_WED_SLEEP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[2][3][0] = Integer.parseInt( st.nextToken() );
					schedules[2][3][1] = Integer.parseInt( st.nextToken() );
					schedules[2][3][2] = Integer.parseInt( st.nextToken() );
					schedules[2][3][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_THU_WAKE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[3][0][0] = Integer.parseInt( st.nextToken() );
					schedules[3][0][1] = Integer.parseInt( st.nextToken() );
					schedules[3][0][2] = Integer.parseInt( st.nextToken() );
					schedules[3][0][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_THU_LEAVE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[3][1][0] = Integer.parseInt( st.nextToken() );
					schedules[3][1][1] = Integer.parseInt( st.nextToken() );
					schedules[3][1][2] = Integer.parseInt( st.nextToken() );
					schedules[3][1][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_THU_RETURN) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[3][2][0] = Integer.parseInt( st.nextToken() );
					schedules[3][2][1] = Integer.parseInt( st.nextToken() );
					schedules[3][2][2] = Integer.parseInt( st.nextToken() );
					schedules[3][2][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_THU_SLEEP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[3][3][0] = Integer.parseInt( st.nextToken() );
					schedules[3][3][1] = Integer.parseInt( st.nextToken() );
					schedules[3][3][2] = Integer.parseInt( st.nextToken() );
					schedules[3][3][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_FRI_WAKE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[4][0][0] = Integer.parseInt( st.nextToken() );
					schedules[4][0][1] = Integer.parseInt( st.nextToken() );
					schedules[4][0][2] = Integer.parseInt( st.nextToken() );
					schedules[4][0][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_FRI_LEAVE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[4][1][0] = Integer.parseInt( st.nextToken() );
					schedules[4][1][1] = Integer.parseInt( st.nextToken() );
					schedules[4][1][2] = Integer.parseInt( st.nextToken() );
					schedules[4][1][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_FRI_RETURN) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[4][2][0] = Integer.parseInt( st.nextToken() );
					schedules[4][2][1] = Integer.parseInt( st.nextToken() );
					schedules[4][2][2] = Integer.parseInt( st.nextToken() );
					schedules[4][2][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_FRI_SLEEP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[4][3][0] = Integer.parseInt( st.nextToken() );
					schedules[4][3][1] = Integer.parseInt( st.nextToken() );
					schedules[4][3][2] = Integer.parseInt( st.nextToken() );
					schedules[4][3][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SAT_WAKE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[5][0][0] = Integer.parseInt( st.nextToken() );
					schedules[5][0][1] = Integer.parseInt( st.nextToken() );
					schedules[5][0][2] = Integer.parseInt( st.nextToken() );
					schedules[5][0][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SAT_LEAVE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[5][1][0] = Integer.parseInt( st.nextToken() );
					schedules[5][1][1] = Integer.parseInt( st.nextToken() );
					schedules[5][1][2] = Integer.parseInt( st.nextToken() );
					schedules[5][1][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SAT_RETURN) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[5][2][0] = Integer.parseInt( st.nextToken() );
					schedules[5][2][1] = Integer.parseInt( st.nextToken() );
					schedules[5][2][2] = Integer.parseInt( st.nextToken() );
					schedules[5][2][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SAT_SLEEP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[5][3][0] = Integer.parseInt( st.nextToken() );
					schedules[5][3][1] = Integer.parseInt( st.nextToken() );
					schedules[5][3][2] = Integer.parseInt( st.nextToken() );
					schedules[5][3][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SUN_WAKE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[6][0][0] = Integer.parseInt( st.nextToken() );
					schedules[6][0][1] = Integer.parseInt( st.nextToken() );
					schedules[6][0][2] = Integer.parseInt( st.nextToken() );
					schedules[6][0][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SUN_LEAVE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[6][1][0] = Integer.parseInt( st.nextToken() );
					schedules[6][1][1] = Integer.parseInt( st.nextToken() );
					schedules[6][1][2] = Integer.parseInt( st.nextToken() );
					schedules[6][1][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SUN_RETURN) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[6][2][0] = Integer.parseInt( st.nextToken() );
					schedules[6][2][1] = Integer.parseInt( st.nextToken() );
					schedules[6][2][2] = Integer.parseInt( st.nextToken() );
					schedules[6][2][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SUN_SLEEP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[6][3][0] = Integer.parseInt( st.nextToken() );
					schedules[6][3][1] = Integer.parseInt( st.nextToken() );
					schedules[6][3][2] = Integer.parseInt( st.nextToken() );
					schedules[6][3][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_OUTDOOR_TEMP) {
					dynamicData.setOutdoorTemperature( Integer.parseInt(dataValue) );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_FILTER) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					dynamicData.setFilterRemaining( Integer.parseInt(st.nextToken()) );
					dynamicData.setFilterRestart( Integer.parseInt(st.nextToken()) );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SETPOINT_LIMITS) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					dynamicData.setLowerCoolSetpointLimit( Integer.parseInt(st.nextToken()) );
					dynamicData.setUpperHeatSetpointLimit( Integer.parseInt(st.nextToken()) );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_RUNTIMES) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					dynamicData.setCoolRuntime( Integer.parseInt(st.nextToken()) );
					dynamicData.setHeatRuntime( Integer.parseInt(st.nextToken()) );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_BATTERY) {
					dynamicData.setBattery( dataValue );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_STRING) {
					infoStrings.add( dataValue );
				}
			}
			catch (Exception e) {
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
		}
		
		// Set the schedule name to "(none)", so that the schedule will be considered "unnamed" and get updated
		getThermostatSchedule().setScheduleName( CtiUtilities.STRING_NONE);
		
		int mondayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_MONDAY ).getEntryID();
		int tuesdayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_TUESDAY ).getEntryID();
		int wednesdayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_WEDNESDAY ).getEntryID();
		int thursdayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_THURSDAY ).getEntryID();
		int fridayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_FRIDAY ).getEntryID();
		int saturdayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY ).getEntryID();
		int sundayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY ).getEntryID();
		
		ArrayList seasons = getThermostatSchedule().getThermostatSeasons();
		for (int i = 0; i < seasons.size(); i++) {
			LiteLMThermostatSeason season = (LiteLMThermostatSeason) seasons.get(i);
			
			int dim3 = (season.getWebConfigurationID() == ECUtils.YUK_WEB_CONFIG_ID_COOL) ? 2 : 3;
			int[] towCnt = { 0, 0, 0, 0, 0, 0, 0 };
			
			for (int j = 0; j < season.getSeasonEntries().size(); j++) {
				LiteLMThermostatSeasonEntry entry = (LiteLMThermostatSeasonEntry) season.getSeasonEntries().get(j);
				int dim1 = 0, dim2 = 0;
				if (entry.getTimeOfWeekID() == mondayID) {
					dim1 = 0;
					dim2 = towCnt[0]++;
				}
				else if (entry.getTimeOfWeekID() == tuesdayID) {
					dim1 = 1;
					dim2 = towCnt[1]++;
				}
				else if (entry.getTimeOfWeekID() == wednesdayID) {
					dim1 = 2;
					dim2 = towCnt[2]++;
				}
				else if (entry.getTimeOfWeekID() == thursdayID) {
					dim1 = 3;
					dim2 = towCnt[3]++;
				}
				else if (entry.getTimeOfWeekID() == fridayID) {
					dim1 = 4;
					dim2 = towCnt[4]++;
				}
				else if (entry.getTimeOfWeekID() == saturdayID) {
					dim1 = 5;
					dim2 = towCnt[5]++;
				}
				else if (entry.getTimeOfWeekID() == sundayID) {
					dim1 = 6;
					dim2 = towCnt[6]++;
				}
				
				int hour = schedules[dim1][dim2][0];
				int minute = schedules[dim1][dim2][1];
				int temp = schedules[dim1][dim2][dim3];
				if (hour == -1) {
					hour = minute = 0;
					temp = -1;
				}
				
				entry.setStartTime( hour * 3600 + minute * 60 );
				entry.setTemperature( temp );
			}
		}
	}

	/**
	 * Returns the thermostatOption.
	 * @return LiteLMThermostatManualOption
	 */
	public ArrayList getThermostatManualEvents() {
		if (thermostatManualEvents == null)
			thermostatManualEvents = new ArrayList();
		return thermostatManualEvents;
	}

	/**
	 * Sets the thermostatOption.
	 * @param thermostatOption The thermostatOption to set
	 */
	public void setThermostatManualEvents(ArrayList thermostatManualEvents) {
		this.thermostatManualEvents = thermostatManualEvents;
	}

	/**
	 * Returns the dynamicData.
	 * @return LiteStarsGatewayEndDevice
	 */
	public LiteStarsGatewayEndDevice getDynamicData() {
		return dynamicData;
	}

	/**
	 * Sets the dynamicData.
	 * @param dynamicData The dynamicData to set
	 */
	public void setDynamicData(LiteStarsGatewayEndDevice dynamicData) {
		this.dynamicData = dynamicData;
	}

	/**
	 * @return
	 */
	public LiteLMThermostatSchedule getThermostatSchedule() {
		return thermostatSchedule;
	}

	/**
	 * @param schedule
	 */
	public void setThermostatSchedule(LiteLMThermostatSchedule schedule) {
		thermostatSchedule = schedule;
	}

}
