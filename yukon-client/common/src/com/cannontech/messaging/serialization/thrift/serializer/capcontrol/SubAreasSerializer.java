package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlMessage;
import com.cannontech.message.capcontrol.model.SubAreas;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCArea;
import com.cannontech.messaging.serialization.thrift.generated.CCGeoAreas;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class SubAreasSerializer extends
    ThriftInheritanceSerializer<SubAreas, CapControlMessage, CCGeoAreas, CCMessage> {

    public SubAreasSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SubAreas> getTargetMessageClass() {
        return SubAreas.class;
    }

    @Override
    protected CCGeoAreas createThriftEntityInstance(CCMessage entityParent) {
        CCGeoAreas entity = new CCGeoAreas();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCGeoAreas entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SubAreas createMessageInstance() {
        return new SubAreas();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCGeoAreas entity, SubAreas msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setMsgInfoBitMask(entity.get_msgInfoBitMask());
        msg.setAreas(helper.convertToMessageList(entity.get_ccGeoAreas(), Area.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SubAreas msg, CCGeoAreas entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        entity.set_msgInfoBitMask(msg.getMsgInfoBitMask());
        entity.set_ccGeoAreas(helper.convertToEntityList(msg.getAreas(), CCArea.class));
    }
}
