package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsGatewayEndDevice extends LiteBase {
	
	private long timestamp = 0;
	private int displayedTemperature = Integer.MIN_VALUE;
	private String displayedTempUnit = null;
	private int coolSetpoint = Integer.MIN_VALUE;
	private int heatSetpoint = Integer.MIN_VALUE;
	private String setpointStatus = null;
	private int fanSwitch = CtiUtilities.NONE_ZERO_ID;
	private int systemSwitch = CtiUtilities.NONE_ZERO_ID;
	private int lastSystemSwitch = CtiUtilities.NONE_ZERO_ID;
	private int lowerCoolSetpointLimit = Integer.MIN_VALUE;
	private int upperHeatSetpointLimit = Integer.MIN_VALUE;
	private int outdoorTemperature = Integer.MIN_VALUE;
	private int filterRemaining = 0;
	private int filterRestart = 0;
	private int coolRuntime = 0;
	private int heatRuntime = 0;
	private String battery = null;
	
	private ArrayList infoStrings = null;
	
	public LiteStarsGatewayEndDevice() {
		super();
		setLiteType( LiteTypes.STARS_GATEWAY_END_DEVICE );
	}
	
	public LiteStarsGatewayEndDevice(int inventoryID) {
		super();
		setInventoryID( inventoryID );
		setLiteType( LiteTypes.STARS_GATEWAY_END_DEVICE );
	}

	/**
	 * Returns the coolSetpoint.
	 * @return int
	 */
	public int getCoolSetpoint() {
		return coolSetpoint;
	}

	/**
	 * Returns the displayedTemperature.
	 * @return int
	 */
	public int getDisplayedTemperature() {
		return displayedTemperature;
	}

	/**
	 * Returns the displayedTempUnit.
	 * @return String
	 */
	public String getDisplayedTempUnit() {
		return displayedTempUnit;
	}

	/**
	 * Returns the fanSwitch.
	 * @return int
	 */
	public int getFanSwitch() {
		return fanSwitch;
	}

	/**
	 * Returns the heatSetpoint.
	 * @return int
	 */
	public int getHeatSetpoint() {
		return heatSetpoint;
	}

	/**
	 * Returns the infoStrings.
	 * @return ArrayList
	 */
	public ArrayList getInfoStrings() {
		if (infoStrings == null)
			infoStrings = new ArrayList();
		return infoStrings;
	}

	/**
	 * Returns the inventoryID.
	 * @return int
	 */
	public int getInventoryID() {
		return getLiteID();
	}

	/**
	 * Returns the lowerCoolSetpointLimit.
	 * @return int
	 */
	public int getLowerCoolSetpointLimit() {
		return lowerCoolSetpointLimit;
	}

	/**
	 * Returns the outdoorTemperature.
	 * @return int
	 */
	public int getOutdoorTemperature() {
		return outdoorTemperature;
	}

	/**
	 * Returns the systemSwitch.
	 * @return int
	 */
	public int getSystemSwitch() {
		return systemSwitch;
	}

	/**
	 * Returns the timestamp.
	 * @return long
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns the upperHeatSetpointLimit.
	 * @return int
	 */
	public int getUpperHeatSetpointLimit() {
		return upperHeatSetpointLimit;
	}

	/**
	 * Sets the coolSetpoint.
	 * @param coolSetpoint The coolSetpoint to set
	 */
	public void setCoolSetpoint(int coolSetpoint) {
		this.coolSetpoint = coolSetpoint;
	}

	/**
	 * Sets the displayedTemperature.
	 * @param displayedTemperature The displayedTemperature to set
	 */
	public void setDisplayedTemperature(int displayedTemperature) {
		this.displayedTemperature = displayedTemperature;
	}

	/**
	 * Sets the displayedTempUnit.
	 * @param displayedTempUnit The displayedTempUnit to set
	 */
	public void setDisplayedTempUnit(String displayedTempUnit) {
		this.displayedTempUnit = displayedTempUnit;
	}

	/**
	 * Sets the fanSwitch.
	 * @param fanSwitch The fanSwitch to set
	 */
	public void setFanSwitch(int fanSwitch) {
		this.fanSwitch = fanSwitch;
	}

	/**
	 * Sets the heatSetpoint.
	 * @param heatSetpoint The heatSetpoint to set
	 */
	public void setHeatSetpoint(int heatSetpoint) {
		this.heatSetpoint = heatSetpoint;
	}

	/**
	 * Sets the infoStrings.
	 * @param infoStrings The infoStrings to set
	 */
	public void setInfoStrings(ArrayList infoStrings) {
		this.infoStrings = infoStrings;
	}

	/**
	 * Sets the inventoryID.
	 * @param inventoryID The inventoryID to set
	 */
	public void setInventoryID(int inventoryID) {
		setLiteID( inventoryID );
	}

	/**
	 * Sets the lowerCoolSetpointLimit.
	 * @param lowerCoolSetpointLimit The lowerCoolSetpointLimit to set
	 */
	public void setLowerCoolSetpointLimit(int lowerCoolSetpointLimit) {
		this.lowerCoolSetpointLimit = lowerCoolSetpointLimit;
	}

	/**
	 * Sets the outdoorTemperature.
	 * @param outdoorTemperature The outdoorTemperature to set
	 */
	public void setOutdoorTemperature(int outdoorTemperature) {
		this.outdoorTemperature = outdoorTemperature;
	}

	/**
	 * Sets the systemSwitch.
	 * @param systemSwitch The systemSwitch to set
	 */
	public void setSystemSwitch(int systemSwitch) {
		this.systemSwitch = systemSwitch;
	}

	/**
	 * Sets the timestamp.
	 * @param timestamp The timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Sets the upperHeatSetpointLimit.
	 * @param upperHeatSetpointLimit The upperHeatSetpointLimit to set
	 */
	public void setUpperHeatSetpointLimit(int upperHeatSetpointLimit) {
		this.upperHeatSetpointLimit = upperHeatSetpointLimit;
	}

	/**
	 * Returns the setpointStatus.
	 * @return String
	 */
	public String getSetpointStatus() {
		return setpointStatus;
	}

	/**
	 * Sets the setpointStatus.
	 * @param setpointStatus The setpointStatus to set
	 */
	public void setSetpointStatus(String setpointStatus) {
		this.setpointStatus = setpointStatus;
	}

	/**
	 * Returns the filterRemaining.
	 * @return int
	 */
	public int getFilterRemaining() {
		return filterRemaining;
	}

	/**
	 * Returns the filterRestart.
	 * @return int
	 */
	public int getFilterRestart() {
		return filterRestart;
	}

	/**
	 * Sets the filterRemaining.
	 * @param filterRemaining The filterRemaining to set
	 */
	public void setFilterRemaining(int filterRemaining) {
		this.filterRemaining = filterRemaining;
	}

	/**
	 * Sets the filterRestart.
	 * @param filterRestart The filterRestart to set
	 */
	public void setFilterRestart(int filterRestart) {
		this.filterRestart = filterRestart;
	}

	/**
	 * Returns the battery.
	 * @return String
	 */
	public String getBattery() {
		return battery;
	}

	/**
	 * Returns the coolRuntime.
	 * @return int
	 */
	public int getCoolRuntime() {
		return coolRuntime;
	}

	/**
	 * Returns the heatRuntime.
	 * @return int
	 */
	public int getHeatRuntime() {
		return heatRuntime;
	}

	/**
	 * Sets the battery.
	 * @param battery The battery to set
	 */
	public void setBattery(String battery) {
		this.battery = battery;
	}

	/**
	 * Sets the coolRuntime.
	 * @param coolRuntime The coolRuntime to set
	 */
	public void setCoolRuntime(int coolRuntime) {
		this.coolRuntime = coolRuntime;
	}

	/**
	 * Sets the heatRuntime.
	 * @param heatRuntime The heatRuntime to set
	 */
	public void setHeatRuntime(int heatRuntime) {
		this.heatRuntime = heatRuntime;
	}

	/**
	 * Returns the lastSystemSwitch.
	 * @return int
	 */
	public int getLastSystemSwitch() {
		return lastSystemSwitch;
	}

	/**
	 * Sets the lastSystemSwitch.
	 * @param lastSystemSwitch The lastSystemSwitch to set
	 */
	public void setLastSystemSwitch(int lastSystemSwitch) {
		this.lastSystemSwitch = lastSystemSwitch;
	}

}
