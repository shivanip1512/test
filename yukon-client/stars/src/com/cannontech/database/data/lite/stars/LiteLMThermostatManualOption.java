package com.cannontech.database.data.lite.stars;

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
public class LiteLMThermostatManualOption extends LiteBase {

	private int previousTemperature = 0;
	private boolean holdTemperature = false;
	private int operationStateID = com.cannontech.common.util.CtiUtilities.NONE_ID;
	private int fanOperationID = com.cannontech.common.util.CtiUtilities.NONE_ID;
	
	public LiteLMThermostatManualOption() {
		super();
		setLiteType( LiteTypes.STARS_THERMOSTAT_MANUAL_OPTION );
	}
	
	public LiteLMThermostatManualOption(int invID) {
		super();
		setInventoryID( invID );
		setLiteType( LiteTypes.STARS_THERMOSTAT_MANUAL_OPTION );
	}
	
	/**
	 * Returns the fanOperationID.
	 * @return int
	 */
	public int getFanOperationID() {
		return fanOperationID;
	}

	/**
	 * Returns the holdTemperature.
	 * @return boolean
	 */
	public boolean isHoldTemperature() {
		return holdTemperature;
	}

	public int getInventoryID() {
		return getLiteID();
	}

	public void setInventoryID(int inventoryID) {
		setLiteID( inventoryID );
	}

	/**
	 * Returns the operationStateID.
	 * @return int
	 */
	public int getOperationStateID() {
		return operationStateID;
	}

	/**
	 * Returns the previousTemperature.
	 * @return int
	 */
	public int getPreviousTemperature() {
		return previousTemperature;
	}

	/**
	 * Sets the fanOperationID.
	 * @param fanOperationID The fanOperationID to set
	 */
	public void setFanOperationID(int fanOperationID) {
		this.fanOperationID = fanOperationID;
	}

	/**
	 * Sets the holdTemperature.
	 * @param holdTemperature The holdTemperature to set
	 */
	public void setHoldTemperature(boolean holdTemperature) {
		this.holdTemperature = holdTemperature;
	}

	/**
	 * Sets the operationStateID.
	 * @param operationStateID The operationStateID to set
	 */
	public void setOperationStateID(int operationStateID) {
		this.operationStateID = operationStateID;
	}

	/**
	 * Sets the previousTemperature.
	 * @param previousTemperature The previousTemperature to set
	 */
	public void setPreviousTemperature(int previousTemperature) {
		this.previousTemperature = previousTemperature;
	}

}
