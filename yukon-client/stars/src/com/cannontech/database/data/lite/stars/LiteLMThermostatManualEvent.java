package com.cannontech.database.data.lite.stars;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteLMThermostatManualEvent extends LiteLMCustomerEvent {

	private int inventoryID = CtiUtilities.NONE_ZERO_ID;
	private int previousTemperature = 0;
	private boolean holdTemperature = false;
	private int operationStateID = CtiUtilities.NONE_ZERO_ID;
	private int fanOperationID = CtiUtilities.NONE_ZERO_ID;
	
	public LiteLMThermostatManualEvent() {
		super();
		setLiteType( LiteTypes.STARS_LMTHERMOSTAT_MANUAL_EVENT );
	}
	
	public LiteLMThermostatManualEvent(int eventID) {
		super();
		setEventID( eventID );
		setLiteType( LiteTypes.STARS_LMTHERMOSTAT_MANUAL_EVENT );
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

	/**
	 * Returns the inventoryID.
	 * @return int
	 */
	public int getInventoryID() {
		return inventoryID;
	}

	/**
	 * Sets the inventoryID.
	 * @param inventoryID The inventoryID to set
	 */
	public void setInventoryID(int inventoryID) {
		this.inventoryID = inventoryID;
	}

}
