package com.cannontech.loadcontrol;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;

public class CollectableMappings 
{
	private static DefineCollectable[] mappings =
	{
		//messages sent
		new com.cannontech.loadcontrol.messages.DefineCollectableLMManualControlMsg(),		
		new com.cannontech.loadcontrol.messages.DefineCollectableLMCommand(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMControlAreaMsg(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMCurtailmentAcknowledgeMsg(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMManualControlMsg(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMMessage(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMEnergyExchangeAcceptMsg(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMEnergyExchangeControlMsg(),
		
		//data received
		new com.cannontech.message.dispatch.message.DefineCollectableMulti(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMGroupVersacom(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMGroupRipple(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMControlArea(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMProgramDirect(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMProgramDirectGear(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMControlAreaTrigger(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMProgramCurtailment(),		
		new com.cannontech.loadcontrol.data.DefineCollectableLMCurtailCustomer(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMEnergyExchangeCustomerReply(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMEnergyExchangeHourlyCustomer(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMEnergyExchangeOffer(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMEnergyExchangeOfferRevision(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMEnergyExchangeCustomer(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMEnergyExchangeHourlyOffer(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMProgramEnergyExchange(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMGroupEmetcon(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMProgramControlWindow(),
		new com.cannontech.loadcontrol.data.DefineCollectableLMGroupPoint()
	};
/**
 * This method was created in VisualAge.
 * @return DefineCollectable[]
 */
public static DefineCollectable[] getMappings() {
	return mappings;
}
}
