package com.cannontech.message.dispatch;

import com.cannontech.message.dispatch.message.DefineCollectableDBChangeMsg;
import com.cannontech.message.dispatch.message.DefineCollectableMulti;
import com.cannontech.message.dispatch.message.DefineCollectablePointData;
import com.cannontech.message.dispatch.message.DefineCollectablePointRegistration;
import com.cannontech.message.dispatch.message.DefineCollectableRegistration;
import com.cannontech.message.dispatch.message.DefineCollectableSignal;
import com.cannontech.message.dispatch.message.DefineCollectableTagMsg;
import com.cannontech.thirdparty.messaging.rw.DefineCollectableControlHistory;
import com.roguewave.vsj.DefineCollectable;

public class CollectableMappings {

	private static DefineCollectable[] mappings =
	{		
		new DefineCollectableMulti(),
		new DefineCollectablePointRegistration(),
		new DefineCollectableRegistration(),
		new DefineCollectablePointData(),
		new DefineCollectableDBChangeMsg(),
		new DefineCollectableSignal(),
		new DefineCollectableTagMsg(),
		new DefineCollectableControlHistory()
	};
	
    public static DefineCollectable[] getMappings() {
    	return mappings;
    }
}
