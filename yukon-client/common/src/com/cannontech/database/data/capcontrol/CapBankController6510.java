package com.cannontech.database.data.capcontrol;

import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.db.DBCopiable;

public class CapBankController6510 extends DNPBase implements DBCopiable, ICapBankController {
	public void setCommID( Integer comID ) {
		getDeviceDirectCommSettings().setPortID( comID );
	}

	public Integer getCommID() {
		return getDeviceDirectCommSettings().getPortID();
	}
}