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
	
	private int inventoryID = 0;
	
	public LiteLMHardwareEvent() {
		super();
		setLiteType( LiteTypes.STARS_LMHARDWARE_EVENT );
	}
	
	public LiteLMHardwareEvent(int eventID) {
		super( eventID );
		setLiteType( LiteTypes.STARS_LMHARDWARE_EVENT );
	}
	
	/**
	 * @return
	 */
	public int getInventoryID() {
		return inventoryID;
	}

	/**
	 * @param i
	 */
	public void setInventoryID(int i) {
		inventoryID = i;
	}

}
