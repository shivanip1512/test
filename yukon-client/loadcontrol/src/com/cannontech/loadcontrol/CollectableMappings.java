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
		new com.cannontech.message.server.DefineCollectableServerResponse(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMManualControlRequest(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMCommand(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMControlAreaMsg(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMCurtailmentAcknowledgeMsg(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMManualControlRequest(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMMessage(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMEnergyExchangeAcceptMsg(),
		new com.cannontech.loadcontrol.messages.DefineCollectableLMEnergyExchangeControlMsg(),
		
		//data received
		new com.cannontech.message.server.DefineCollectableServerRequest(),
		new com.cannontech.message.dispatch.message.DefineCollectableMulti(),
		
		new com.cannontech.loadcontrol.messages.DefineCollectableLMManualControlResponse(),
		
		new com.cannontech.loadcontrol.data.DefColl_LMControlArea(),
		new com.cannontech.loadcontrol.data.DefColl_LMProgramDirect(),
		new com.cannontech.loadcontrol.data.DefColl_LMProgramDirectGear(),
		new com.cannontech.loadcontrol.data.DefColl_LMControlAreaTrigger(),
		new com.cannontech.loadcontrol.data.DefColl_LMProgramCurtailment(),		
		new com.cannontech.loadcontrol.data.DefColl_LMCurtailCustomer(),
		new com.cannontech.loadcontrol.data.DefColl_LMEnergyExchangeCustomerReply(),
		new com.cannontech.loadcontrol.data.DefColl_LMEnergyExchangeHourlyCustomer(),
		new com.cannontech.loadcontrol.data.DefColl_LMEnergyExchangeOffer(),
		new com.cannontech.loadcontrol.data.DefColl_LMEnergyExchangeOfferRevision(),
		new com.cannontech.loadcontrol.data.DefColl_LMEnergyExchangeCustomer(),
		new com.cannontech.loadcontrol.data.DefColl_LMEnergyExchangeHourlyOffer(),
		new com.cannontech.loadcontrol.data.DefColl_LMProgramEnergyExchange(),
		
		new com.cannontech.loadcontrol.data.DefColl_LMProgramControlWindow(),
      	new com.cannontech.loadcontrol.data.DefColl_LMProgramThermostatGear(),
      
	 	new com.cannontech.loadcontrol.data.DefColl_LMGroupEmetcon(),
	  	new com.cannontech.loadcontrol.data.DefColl_LMGroupExpresscom(),
	  	new com.cannontech.loadcontrol.data.DefColl_LMGroupMCT(),
	  	new com.cannontech.loadcontrol.data.DefColl_LMGroupPoint(),
	  	new com.cannontech.loadcontrol.data.DefColl_LMGroupRipple(),
		new com.cannontech.loadcontrol.data.DefColl_LMGroupSA105(),
		new com.cannontech.loadcontrol.data.DefColl_LMGroupSA205(),
		new com.cannontech.loadcontrol.data.DefColl_LMGroupSA305(),
		new com.cannontech.loadcontrol.data.DefColl_LMGroupSADigital(),
		new com.cannontech.loadcontrol.data.DefColl_LMGroupGolay(),
	    new com.cannontech.loadcontrol.data.DefColl_LMGroupVersacom()
	};
   

/**
 * This method was created in VisualAge.
 * @return DefineCollectable[]
 */
public static DefineCollectable[] getMappings() {
	return mappings;
}
}
