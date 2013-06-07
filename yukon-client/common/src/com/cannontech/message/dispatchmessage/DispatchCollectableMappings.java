package com.cannontech.message.dispatchmessage;

import com.cannontech.message.thirdparty.DefineCollectableControlHistory;
import com.roguewave.vsj.DefineCollectable;

public class DispatchCollectableMappings {

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
