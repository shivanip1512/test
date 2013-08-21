package com.cannontech.messaging.serialization.thrift.test;

import org.junit.Test;

public class LoadControlMessagesAutoSerializationTest extends MessageAutoSerializationTestBase {

    @Override
    protected String[] getContextUri() {
        return new String[] { "com/cannontech/messaging/thriftConnectionContext.xml",
                             "com/cannontech/messaging/thriftLoadControlConnectionContext.xml",
                             "com/cannontech/messaging/serialization/thrift/test/messagevalidator/messageValidatorContext.xml" };
    }

    @Test
    public void testAllLoadControlAndCommonMessages() {
        testAllMessages();
    }

  //@Test
    public void LMProgramEnergyExchangelMessage() {
        testMessage(com.cannontech.loadcontrol.data.LMProgramEnergyExchange.class);        
    }
    
    //@Test
    public void autoTestLoadControlMessages() {
        testMessage(com.cannontech.loadcontrol.messages.LMMessage.class);
        testMessage(com.cannontech.loadcontrol.messages.LMCommand.class);
        // testMessage(com.cannontech.loadcontrol.messages.LMControlAreaMsg.class); // Serialize method is not
        // implemented (only deserialize)
        testMessage(com.cannontech.loadcontrol.messages.ConstraintViolation.class);
        testMessage(com.cannontech.loadcontrol.messages.LMCurtailmentAcknowledgeMsg.class);
        testMessage(com.cannontech.loadcontrol.messages.LMEnergyExchangeAcceptMsg.class);
        testMessage(com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.class);
        testMessage(com.cannontech.loadcontrol.messages.LMManualControlRequest.class);
        testMessage(com.cannontech.loadcontrol.messages.LMManualControlResponse.class);
        // testMessage(com.cannontech.loadcontrol.messages.LMOfferCustomerResponse.class); // Serialize method is not
        // implemented (only deserialize)
        // testMessage(com.cannontech.loadcontrol.messages.LMOfferSubmission.class); // Serialize method is not
        // implemented (only deserialize)
    }

    //@Test
    public void autoTestDynamicReceiveMessages() {
        testMessage(com.cannontech.loadcontrol.dynamic.receive.LMControlAreaChanged.class);
        testMessage(com.cannontech.loadcontrol.dynamic.receive.LMGroupChanged.class);
        testMessage(com.cannontech.loadcontrol.dynamic.receive.LMProgramChanged.class);
        testMessage(com.cannontech.loadcontrol.dynamic.receive.LMTriggerChanged.class);
    }

    //@Test
    public void autoTestDataMessages() {

        // Groups can not be sent as their serialize method is not implemented (only deserialize)
        // testMessage(com.cannontech.loadcontrol.data.LMGroupBase.class);
        // testMessage(com.cannontech.loadcontrol.data.LMDirectGroupBase.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupDigiSep.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupEmetcon.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupExpresscom.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupGolay.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupMCT.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupPoint.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupRipple.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupSA105.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupSA205.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupSA305.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupSADigital.class);
        // testMessage(com.cannontech.loadcontrol.data.LMGroupVersacom.class);

        // Programs can not be sent as their serialize method is not implemented (only deserialize)
        // testMessage(com.cannontech.loadcontrol.data.LMProgramBase.class); // abstract
        // testMessage(com.cannontech.loadcontrol.data.LMProgramControlWindow.class);
        // testMessage(com.cannontech.loadcontrol.data.LMProgramCurtailment.class);
        // testMessage(com.cannontech.loadcontrol.data.LMProgramDirectGear.class);
        // testMessage(com.cannontech.loadcontrol.data.LMProgramDirect.class);
        // testMessage(com.cannontech.loadcontrol.data.LMProgramEnergyExchange.class);

        testMessage(com.cannontech.loadcontrol.data.LMCICustomerBase.class);
        testMessage(com.cannontech.loadcontrol.data.LMCurtailCustomer.class);
        testMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer.class);
        testMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply.class);
        testMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer.class);
        testMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer.class);
        testMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeOffer.class);
        testMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision.class);

        // testMessage(com.cannontech.loadcontrol.data.LMControlArea.class); // Serialize method is not implemented
        // (only deserialize)
        // testMessage(com.cannontech.loadcontrol.data.LMControlAreaTrigger.class); // Serialize method is not
        // implemented (only deserialize)
    }
}
