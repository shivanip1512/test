package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.stars.web.servlet.SOAPServer;

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
				
	private ArrayList thermostatSeasons = null;		// List of LiteLMThermostatSeason
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
	
	public void updateThermostatSettings(LiteStarsEnergyCompany energyCompany) {
		try {
			LiteLMHardwareBase liteHw = energyCompany.getLMHardware( getInventoryID(), true );
			int hwTypeDefID = energyCompany.getYukonListEntry(
					YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, liteHw.getLmHardwareTypeID()
					).getYukonDefID();
			Object[][] data = com.cannontech.database.db.stars.hardware.GatewayEndDevice.getHardwareData(
					liteHw.getManufactureSerialNumber(), new Integer(hwTypeDefID) );
					
			/* Thermostat schedules
			 * First dimension: weekday, saturday, sunday
			 * Second dimension: wake, leave, return, sleep
			 * Third dimension: hour, minute, cool setpoint, heat setpoint
			 */
			int[][][] schedules = new int[3][4][4];
			for (int i = 0; i < 3; i++) 
				for (int j= 0; j < 4; j++)
					schedules[i][j][0] = -1;	// -1 means values of schedules[i][j][0...3] haven't been set yet
			
			ArrayList infoStrings = new ArrayList();
			
			int fanStateDftID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT ).getEntryID();
			int fanStateAutoID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_AUTO ).getEntryID();
			int fanStateOnID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_ON ).getEntryID();
			int thermModeDftID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT ).getEntryID();
			int thermModeCoolID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_COOL ).getEntryID();
			int thermModeHeatID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_HEAT ).getEntryID();
			int thermModeOffID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_OFF ).getEntryID();
			
			for (int i = 0; i < data.length; i++) {
				int dataType = ((Integer) data[i][0]).intValue();
				String dataValue = (String) data[i][1];
				
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
					int thermMode = thermModeDftID;
					if (dataValue.equalsIgnoreCase("COOL"))
						thermMode = thermModeCoolID;
					else if (dataValue.equalsIgnoreCase("HEAT"))
						thermMode = thermModeHeatID;
					else if (dataValue.equalsIgnoreCase("OFF"))
						thermMode = thermModeOffID;
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
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SAT_WAKE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[1][0][0] = Integer.parseInt( st.nextToken() );
					schedules[1][0][1] = Integer.parseInt( st.nextToken() );
					schedules[1][0][2] = Integer.parseInt( st.nextToken() );
					schedules[1][0][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SAT_LEAVE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[1][1][0] = Integer.parseInt( st.nextToken() );
					schedules[1][1][1] = Integer.parseInt( st.nextToken() );
					schedules[1][1][2] = Integer.parseInt( st.nextToken() );
					schedules[1][1][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SAT_RETURN) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[1][2][0] = Integer.parseInt( st.nextToken() );
					schedules[1][2][1] = Integer.parseInt( st.nextToken() );
					schedules[1][2][2] = Integer.parseInt( st.nextToken() );
					schedules[1][2][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SAT_SLEEP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[1][3][0] = Integer.parseInt( st.nextToken() );
					schedules[1][3][1] = Integer.parseInt( st.nextToken() );
					schedules[1][3][2] = Integer.parseInt( st.nextToken() );
					schedules[1][3][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SUN_WAKE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[2][0][0] = Integer.parseInt( st.nextToken() );
					schedules[2][0][1] = Integer.parseInt( st.nextToken() );
					schedules[2][0][2] = Integer.parseInt( st.nextToken() );
					schedules[2][0][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SUN_LEAVE) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[2][1][0] = Integer.parseInt( st.nextToken() );
					schedules[2][1][1] = Integer.parseInt( st.nextToken() );
					schedules[2][1][2] = Integer.parseInt( st.nextToken() );
					schedules[2][1][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SUN_RETURN) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[2][2][0] = Integer.parseInt( st.nextToken() );
					schedules[2][2][1] = Integer.parseInt( st.nextToken() );
					schedules[2][2][2] = Integer.parseInt( st.nextToken() );
					schedules[2][2][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SCHEDULE_SUN_SLEEP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					String fan = st.nextToken();
					schedules[2][3][0] = Integer.parseInt( st.nextToken() );
					schedules[2][3][1] = Integer.parseInt( st.nextToken() );
					schedules[2][3][2] = Integer.parseInt( st.nextToken() );
					schedules[2][3][3] = Integer.parseInt( st.nextToken() );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_OUTDOOR_TEMP) {
					dynamicData.setOutdoorTemperature( Integer.parseInt(dataValue) );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_SETPOINT_LIMITS) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					dynamicData.setLowerCoolSetpointLimit( Integer.parseInt(st.nextToken()) );
					dynamicData.setUpperHeatSetpointLimit( Integer.parseInt(st.nextToken()) );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_STRING) {
					infoStrings.add( dataValue );
				}
				else {
					dynamicData.getExtraInformation().put( data[i][0], dataValue );
				}
			}
			
			dynamicData.setInfoStrings( infoStrings );
			
			int weekdayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY ).getEntryID();
			int saturdayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY ).getEntryID();
			int sundayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY ).getEntryID();
			
			for (int i = 0; i < thermostatSeasons.size(); i++) {
				LiteLMThermostatSeason season = (LiteLMThermostatSeason) thermostatSeasons.get(i);
				int dim3 = (season.getWebConfigurationID() == SOAPServer.YUK_WEB_CONFIG_ID_COOL) ? 2 : 3;
				int[] towCnt = { 0, 0, 0 };
				
				for (int j = 0; j < season.getSeasonEntries().size(); j++) {
					LiteLMThermostatSeasonEntry entry = (LiteLMThermostatSeasonEntry) season.getSeasonEntries().get(j);
					int dim1 = 0, dim2 = 0;
					if (entry.getTimeOfWeekID() == weekdayID) {
						dim1 = 0;
						dim2 = towCnt[0]++;
					}
					else if (entry.getTimeOfWeekID() == saturdayID) {
						dim1 = 1;
						dim2 = towCnt[1]++;
					}
					else if (entry.getTimeOfWeekID() == sundayID) {
						dim1 = 2;
						dim2 = towCnt[2]++;
					}
					
					if (schedules[dim1][dim2][0] != -1) {
						entry.setStartTime( schedules[dim1][dim2][0] * 3600 + schedules[dim1][dim2][1] * 60 );
						entry.setTemperature( schedules[dim1][dim2][dim3] );
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the thermostatSeasons.
	 * @return java.util.ArrayList
	 */
	public ArrayList getThermostatSeasons() {
		if (thermostatSeasons == null)
			thermostatSeasons = new java.util.ArrayList();
		return thermostatSeasons;
	}

	/**
	 * Sets the thermostatSeasons.
	 * @param thermostatSeasons The thermostatSeasons to set
	 */
	public void setThermostatSeasons(ArrayList thermostatSeasons) {
		this.thermostatSeasons = thermostatSeasons;
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

}
