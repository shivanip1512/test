package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.capcontrol.CapControlMessage;
import com.cannontech.messaging.message.capcontrol.VoltageRegulatorFlagMessage;
import com.cannontech.messaging.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.generated.CCVoltageRegulator;
import com.cannontech.messaging.serialization.thrift.generated.CCVoltageRegulatorItem;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class VoltageRegulatorSerializer extends
    ThriftInheritanceSerializer<VoltageRegulatorFlagMessage, CapControlMessage, CCVoltageRegulator, CCMessage> {

    public VoltageRegulatorSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VoltageRegulatorFlagMessage> getTargetMessageClass() {
        return VoltageRegulatorFlagMessage.class;
    }

    @Override
    protected CCVoltageRegulator createThrifEntityInstance(CCMessage entityParent) {
        CCVoltageRegulator entity = new CCVoltageRegulator();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCVoltageRegulator entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VoltageRegulatorFlagMessage createMessageInstance() {
        return new VoltageRegulatorFlagMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCVoltageRegulator entity, VoltageRegulatorFlagMessage msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setVoltageRegulatorFlags(helper.convertToMessageVector(entity.get_regulators(),
                                                                                  VoltageRegulatorFlags.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VoltageRegulatorFlagMessage msg, CCVoltageRegulator entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        entity.set_regulators(helper.convertToEntityList(msg.getVoltageRegulatorFlags(),
                                                                        CCVoltageRegulatorItem.class));
    }

}
