package com.cannontech.messaging.serialization.thrift.test.autotest;

import org.junit.jupiter.api.Test;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

public class LoadControlMessagesAutoSerializationTest extends MessageAutoSerializationTestBase {

    @Override
    protected String[] getContextUri() {
        return new String[] {"com/cannontech/messaging/testThriftContext.xml",
                             "com/cannontech/messaging/serialization/thrift/test/messagevalidator/messageValidatorContext.xml" };
    }

    @Override
    protected ThriftMessageFactory createMessageFactory() {
        return appContext.getBean("messaging.thrift.LoadcontrolMessageFactory", ThriftMessageFactory.class);
    }

    @Test
    public void autoTestLoadControlMessages() {
        autoTestMessage(com.cannontech.loadcontrol.messages.LMMessage.class);
        autoTestMessage(com.cannontech.loadcontrol.messages.LMCommand.class);
        autoTestMessage(com.cannontech.loadcontrol.messages.LMControlAreaMsg.class);
        autoTestMessage(com.cannontech.loadcontrol.messages.ConstraintViolation.class);
        autoTestMessage(com.cannontech.loadcontrol.messages.LMCurtailmentAcknowledgeMsg.class);
        autoTestMessage(com.cannontech.loadcontrol.messages.LMEnergyExchangeAcceptMsg.class);
        autoTestMessage(com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.class);
        autoTestMessage(com.cannontech.loadcontrol.messages.LMManualControlRequest.class);
        autoTestMessage(com.cannontech.loadcontrol.messages.LMManualControlResponse.class);
    }

    @Test
    public void autoTestDynamicReceiveMessages() {
        autoTestMessage(com.cannontech.loadcontrol.dynamic.receive.LMControlAreaChanged.class);
        autoTestMessage(com.cannontech.loadcontrol.dynamic.receive.LMGroupChanged.class);
        autoTestMessage(com.cannontech.loadcontrol.dynamic.receive.LMProgramChanged.class);
        autoTestMessage(com.cannontech.loadcontrol.dynamic.receive.LMTriggerChanged.class);
    }

    @Test
    public void autoTestDataMessages() {
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupBase.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMDirectGroupBase.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupDigiSep.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupEmetcon.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupExpresscom.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupGolay.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupMCT.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupPoint.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupRipple.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupSA105.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupSA205.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupSA305.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupSADigital.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMGroupVersacom.class);

        autoTestMessage(com.cannontech.loadcontrol.data.LMProgramBase.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMProgramControlWindow.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMProgramCurtailment.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMProgramDirectGear.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMProgramDirect.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMProgramEnergyExchange.class);

        autoTestMessage(com.cannontech.loadcontrol.data.LMCICustomerBase.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMCurtailCustomer.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeOffer.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision.class);

        autoTestMessage(com.cannontech.loadcontrol.data.LMControlArea.class);
        autoTestMessage(com.cannontech.loadcontrol.data.LMControlAreaTrigger.class);
    }
}
