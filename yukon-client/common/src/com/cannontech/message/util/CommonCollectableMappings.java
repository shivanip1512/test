package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;

public class CommonCollectableMappings {

	private static DefineCollectable[] mappings =
	{
		new com.cannontech.message.util.DefineCollectableMessage(),
		new com.cannontech.message.util.DefineCollectablePing(),
		new com.cannontech.message.util.DefineCollectableCommand(),
        new com.cannontech.message.server.DefineCollectableServerRequest(),
        new com.cannontech.message.server.DefineCollectableServerResponse(),
        new com.cannontech.message.dispatchmessage.DefineCollectableMulti()
	};	
    
/**
 * This method was created in VisualAge.
 * @return DefineCollectable[]
 */
public static DefineCollectable[] getMappings() {
	return mappings;
}
}
