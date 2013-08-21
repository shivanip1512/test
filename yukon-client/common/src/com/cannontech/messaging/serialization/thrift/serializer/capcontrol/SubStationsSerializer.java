package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlMessage;
import com.cannontech.message.capcontrol.model.SubStations;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.generated.CCSubstations;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class SubStationsSerializer extends
    ThriftInheritanceSerializer<SubStations, CapControlMessage, CCSubstations, CCMessage> {

    public SubStationsSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SubStations> getTargetMessageClass() {
        return SubStations.class;
    }

    @Override
    protected CCSubstations createThriftEntityInstance(CCMessage entityParent) {
        CCSubstations entity = new CCSubstations();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCSubstations entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SubStations createMessageInstance() {
        return new SubStations();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCSubstations entity,
                                                   SubStations msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setMsgInfoBitMask(entity.get_msgInfoBitMask());
        msg.setSubStations(helper.convertToMessageVector(entity.get_ccSubstations(), SubStation.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SubStations msg,
                                                   CCSubstations entity) {
        throw new UnsupportedOperationException("Message serialization not supported");
    }
}
