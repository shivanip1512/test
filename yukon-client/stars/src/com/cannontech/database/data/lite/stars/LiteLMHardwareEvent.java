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
	
	public LiteLMHardwareEvent() {
		super();
		setLiteType( LiteTypes.STARS_LMHARDWARE_EVENT );
	}
	
	public LiteLMHardwareEvent(int eventID) {
		super( eventID );
		setLiteType( LiteTypes.STARS_LMHARDWARE_EVENT );
	}
	
}
