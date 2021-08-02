package com.cannontech.messaging.serialization.thrift.test.autotest;

import org.junit.jupiter.api.Test;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

public class CapcontrolMessagesAutoSerializationTest  extends MessageAutoSerializationTestBase {

    @Override
    protected ThriftMessageFactory createMessageFactory() {
        return appContext.getBean("messaging.thrift.CapcontrolMessageFactory", ThriftMessageFactory.class);
    }

    @Test
    public void testCapcontrolMessages() {
        autoTestMessage(com.cannontech.message.capcontrol.streamable.Area.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.BankMove.class);
        autoTestMessage(com.cannontech.message.capcontrol.streamable.CapBankDevice.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.CapControlMessage.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.ChangeOpState.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.CapControlMessage.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.DeleteItem.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.DynamicCommand.class);
        autoTestMessage(com.cannontech.message.capcontrol.streamable.Feeder.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.ItemCommand.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.CapControlServerResponse.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.SpecialAreas.class);
        autoTestMessage(com.cannontech.message.capcontrol.streamable.SpecialArea.class);          
        autoTestMessage(com.cannontech.message.capcontrol.model.States.class);
        autoTestMessage(com.cannontech.database.db.state.State.class);               
        autoTestMessage(com.cannontech.message.capcontrol.streamable.StreamableCapObject.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.SubAreas.class);
        autoTestMessage(com.cannontech.message.capcontrol.streamable.SubBus.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.SubstationBuses.class);              
        autoTestMessage(com.cannontech.message.capcontrol.streamable.SubStation.class);   
        autoTestMessage(com.cannontech.message.capcontrol.model.SubStations.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.SystemStatus.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.VerifyBanks.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.VerifyInactiveBanks.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.VerifySelectedBank.class);               
        autoTestMessage(com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags.class);
        autoTestMessage(com.cannontech.message.capcontrol.model.VoltageRegulatorFlagMessage.class);
        
        autoTestMessage(com.cannontech.message.dispatch.message.PointData.class);        
    }
}
