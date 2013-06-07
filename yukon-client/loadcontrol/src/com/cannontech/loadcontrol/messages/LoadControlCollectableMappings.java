package com.cannontech.loadcontrol.messages;

import com.roguewave.vsj.DefineCollectable;

public class LoadControlCollectableMappings {

    private static DefineCollectable[] mappings = {
        // messages sent
        new com.cannontech.message.server.DefineCollectableServerResponse(),
        new DefineCollectableLMManualControlRequest(),
        new DefineCollectableLMCommand(),
        new DefineCollectableLMControlAreaMsg(),
        new DefineCollectableLMCurtailmentAcknowledgeMsg(),
        new DefineCollectableLMManualControlRequest(),
        new DefineCollectableLMMessage(),
        new DefineCollectableLMEnergyExchangeAcceptMsg(),
        new DefineCollectableLMEnergyExchangeControlMsg(),

        // data received
        new com.cannontech.message.server.DefineCollectableServerRequest(),
        new com.cannontech.message.dispatchmessage.DefineCollectableMulti(),

        new DefineCollectableLMManualControlResponse(), new DefineCollectableConstraintViolation(),

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
        new com.cannontech.loadcontrol.data.DefColl_LMGroupVersacom(),
        new com.cannontech.loadcontrol.data.DefColl_LMGroupDigiSep(),

        new com.cannontech.loadcontrol.dynamic.receive.CtiLMDynamicControlAreaDataMsg(),
        new com.cannontech.loadcontrol.dynamic.receive.CtiLMDynamicGroupMsg(),
        new com.cannontech.loadcontrol.dynamic.receive.CtiLMDynamicLMTriggerMsg(),
        new com.cannontech.loadcontrol.dynamic.receive.CtiLMDynamicProgramMsg() };

    public static DefineCollectable[] getMappings() {
        return mappings;
    }
}
