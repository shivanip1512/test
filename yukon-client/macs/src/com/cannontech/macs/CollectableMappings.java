package com.cannontech.macs;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;

public class CollectableMappings {

	private static DefineCollectable[] mappings =
	{

		new com.cannontech.message.macs.message.DefineCollectableInfo(),
		new com.cannontech.message.macs.message.DefineCollectableOverrideRequest(),
		new com.cannontech.message.macs.message.DefineCollectableRetrieveScript(),
		new com.cannontech.message.macs.message.DefineCollectableRetrieveSchedule(),
		new com.cannontech.message.macs.message.DefineCollectableUpdateSchedule(),
		new com.cannontech.message.macs.message.DefineCollectableAddSchedule(),
		new com.cannontech.message.macs.message.DefineCollectableDeleteSchedule(),
		new com.cannontech.message.dispatch.message.DefineCollectableMulti(),
		new com.cannontech.message.macs.message.DefineCollectableScriptFile(),
		new com.cannontech.message.macs.message.DefineCollectableSchedule()
	};

/**
 * This method was created in VisualAge.
 * @return DefineCollectable[]
 */
public static DefineCollectable[] getMappings() {
	return mappings;
}
}
