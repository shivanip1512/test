package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlMessage;
import com.cannontech.message.capcontrol.model.SubstationBuses;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.generated.CCSubstationBus;
import com.cannontech.messaging.serialization.thrift.generated.CCSubstationBusItem;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class SubstationBusesSerializer extends
    ThriftInheritanceSerializer<SubstationBuses, CapControlMessage, CCSubstationBus, CCMessage> {

    public SubstationBusesSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SubstationBuses> getTargetMessageClass() {
        return SubstationBuses.class;
    }

    @Override
    protected CCSubstationBus createThriftEntityInstance(CCMessage entityParent) {
        CCSubstationBus entity = new CCSubstationBus();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCSubstationBus entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SubstationBuses createMessageInstance() {
        return new SubstationBuses();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCSubstationBus entity,
                                                   SubstationBuses msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setMsgInfoBitMask(entity.get_msgInfoBitMask());
        msg.setSubBuses(helper.convertToMessageVector(entity.get_ccSubstationBuses(), SubBus.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SubstationBuses msg,
                                                   CCSubstationBus entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        entity.set_msgInfoBitMask(msg.getMsgInfoBitMask());
        entity.set_ccSubstationBuses(helper.convertToEntityList(msg.getSubBuses(), CCSubstationBusItem.class));
    }

}
