package com.cannontech.message.dispatch;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;


public class CollectableMappings {

	private static DefineCollectable[] mappings =
	{		
		new com.cannontech.message.dispatch.message.DefineCollectableCommand(),
		new com.cannontech.message.dispatch.message.DefineCollectableMulti(),
		new com.cannontech.message.dispatch.message.DefineCollectablePointRegistration(),
		new com.cannontech.message.dispatch.message.DefineCollectableRegistration(),
		new com.cannontech.message.dispatch.message.DefineCollectablePointData(),
		new com.cannontech.message.dispatch.message.DefineCollectableDBChangeMsg(),
		new com.cannontech.message.dispatch.message.DefineCollectableSignal(),
		new com.cannontech.message.dispatch.message.DefineCollectableTagMsg()
	};
/**
 * This method was created in VisualAge.
 * @return com.roguewave.vsj.DefineCollectable[]
 */
public static DefineCollectable[] getMappings() {
	return mappings;
}
}
