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
	private boolean holdTemperature = false;
	private int operationStateID = CtiUtilities.NONE_ZERO_ID;
	private int fanOperationID = CtiUtilities.NONE_ZERO_ID;
	private int previousCoolTemperature = 0;
    private int previousHeatTemperature = 0;
	
	public LiteLMThermostatManualEvent() {
		super();
		setLiteType( LiteTypes.STARS_LMTHERMOSTAT_MANUAL_EVENT );
	}
	
	public LiteLMThermostatManualEvent(int eventID) {
		super();
		setEventID( eventID );
		setLiteType( LiteTypes.STARS_LMTHERMOSTAT_MANUAL_EVENT );
	}
	
	public int getFanOperationID() {
		return fanOperationID;
	}

	public boolean isHoldTemperature() {
		return holdTemperature;
	}

	public int getOperationStateID() {
		return operationStateID;
	}

	public void setFanOperationID(int fanOperationID) {
		this.fanOperationID = fanOperationID;
	}

	public void setHoldTemperature(boolean holdTemperature) {
		this.holdTemperature = holdTemperature;
	}

	public void setOperationStateID(int operationStateID) {
		this.operationStateID = operationStateID;
	}

	public int getInventoryID() {
		return inventoryID;
	}

	public void setInventoryID(int inventoryID) {
		this.inventoryID = inventoryID;
	}

    public int getPreviousCoolTemperature() {
        return previousCoolTemperature;
    }
    public void setPreviousCoolTemperature(int previousCoolTemperature) {
        this.previousCoolTemperature = previousCoolTemperature;
    }

    public int getPreviousHeatTemperature() {
        return previousHeatTemperature;
    }
    public void setPreviousHeatTemperature(int previousHeatTemperature) {
        this.previousHeatTemperature = previousHeatTemperature;
    }
}