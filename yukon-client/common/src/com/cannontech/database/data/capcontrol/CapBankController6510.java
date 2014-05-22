package com.cannontech.database.data.capcontrol;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.db.DBCopiable;

public class CapBankController6510 extends DNPBase implements DBCopiable, ICapBankController {
	
    public CapBankController6510() {
        super(PaoType.DNP_CBC_6510);
    }
    
    @Override
    public void setCommID( Integer comID ) {
		getDeviceDirectCommSettings().setPortID( comID );
	}

	@Override
    public Integer getCommID() {
		return getDeviceDirectCommSettings().getPortID();
	}
}