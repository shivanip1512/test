package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;

public class CollectableMappings {

	private static DefineCollectable[] mappings =
	{
		new com.cannontech.message.util.DefineCollectableMessage(),
		new com.cannontech.message.util.DefineCollectablePing()
	};	
/**
 * This method was created in VisualAge.
 * @return DefineCollectable[]
 */
public static DefineCollectable[] getMappings() {
	return mappings;
}
}
