package com.cannontech.database.data.lite.stars;

import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteLMHardwareEvent extends LiteLMCustomerEvent {
	
	private int inventoryID = com.cannontech.database.db.stars.hardware.InventoryBase.NONE_INT;
	
	public LiteLMHardwareEvent() {
		super();
	}
	
	public LiteLMHardwareEvent(int eventID) {
		super();
		setEventID( eventID );
		setLiteType( LiteTypes.STARS_LMHARDWARE_EVENT );
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
