package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteBase;

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
	
	private List<LiteLMThermostatManualEvent> thermostatManualEvents = null;	// List of LMThermostatManualEvent
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
		int hwTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry(liteHw.getLmHardwareTypeID()).getYukonDefID();
		
		Object[][] data = com.cannontech.database.db.stars.hardware.GatewayEndDevice.getHardwareData(
				liteHw.getManufacturerSerialNumber(), new Integer(hwTypeDefID) );
		if (data == null || data.length == 0) return;
		
		List<String> infoStrings = getDynamicData().getInfoStrings();
		infoStrings.clear();
		
		int fanStateDftID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT ).getEntryID();
		int fanStateAutoID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_AUTO ).getEntryID();
		int fanStateOnID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_ON ).getEntryID();
		int thermModeDftID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT ).getEntryID();
		int thermModeCoolID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_COOL ).getEntryID();
		int thermModeHeatID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_HEAT ).getEntryID();
		int thermModeOffID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_OFF ).getEntryID();
		
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
					dynamicData.setSystemSwitch( thermMode );
				}
				else if (dataType == YukonListEntryTypes.YUK_DEF_ID_GED_DISPLAYED_TEMP) {
					StringTokenizer st = new StringTokenizer( dataValue, "," );
					dynamicData.setDisplayedTemperature( Integer.parseInt(st.nextToken()) );
					dynamicData.setDisplayedTempUnit( st.nextToken() );
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
		
	}

	/**
	 * Returns the thermostatOption.
	 * @return LiteLMThermostatManualOption
	 */
	public List<LiteLMThermostatManualEvent> getThermostatManualEvents() {
		if (thermostatManualEvents == null)
			thermostatManualEvents = new ArrayList<LiteLMThermostatManualEvent>();
		return thermostatManualEvents;
	}

	/**
	 * Sets the thermostatOption.
	 * @param thermostatOption The thermostatOption to set
	 */
	public void setThermostatManualEvents(List<LiteLMThermostatManualEvent> thermostatManualEvents) {
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
