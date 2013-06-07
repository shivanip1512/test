package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.capcontrol.CapControlMessage;
import com.cannontech.messaging.message.capcontrol.SubStationsMessage;
import com.cannontech.messaging.message.capcontrol.streamable.SubStation;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.generated.CCSubstationItem;
import com.cannontech.messaging.serialization.thrift.generated.CCSubstations;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class SubStationsSerializer extends
    ThriftInheritanceSerializer<SubStationsMessage, CapControlMessage, CCSubstations, CCMessage> {

    public SubStationsSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SubStationsMessage> getTargetMessageClass() {
        return SubStationsMessage.class;
    }

    @Override
    protected CCSubstations createThrifEntityInstance(CCMessage entityParent) {
        CCSubstations entity = new CCSubstations();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCSubstations entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SubStationsMessage createMessageInstance() {
        return new SubStationsMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCSubstations entity, SubStationsMessage msg) {
                ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setMsgInfoBitMask(entity.get_msgInfoBitMask());
        msg.setSubStations(helper.convertToMessageVector(entity.get_ccSubstations(), SubStation.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SubStationsMessage msg, CCSubstations entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        entity.set_msgInfoBitMask(msg.getMsgInfoBitMask());
        entity
            .set_ccSubstations(helper.convertToEntityList(msg.getSubStations(), CCSubstationItem.class));
    }
}
