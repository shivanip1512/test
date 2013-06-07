package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.capcontrol.CapControlMessage;
import com.cannontech.messaging.message.capcontrol.SpecialAreasMessage;
import com.cannontech.messaging.message.capcontrol.streamable.SpecialArea;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.generated.CCSpecial;
import com.cannontech.messaging.serialization.thrift.generated.CCSpecialAreas;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class SpecialAreasSerializer extends
    ThriftInheritanceSerializer<SpecialAreasMessage, CapControlMessage, CCSpecialAreas, CCMessage> {

    public SpecialAreasSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SpecialAreasMessage> getTargetMessageClass() {
        return SpecialAreasMessage.class;
    }

    @Override
    protected CCSpecialAreas createThrifEntityInstance(CCMessage entityParent) {
        CCSpecialAreas entity = new CCSpecialAreas();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCSpecialAreas entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SpecialAreasMessage createMessageInstance() {
        return new SpecialAreasMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCSpecialAreas entity, SpecialAreasMessage msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        msg.setAreas(helper.convertToMessageList(entity.get_ccSpecialAreas(), SpecialArea.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SpecialAreasMessage msg, CCSpecialAreas entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        entity.set_ccSpecialAreas(helper.convertToEntityList(msg.getAreas(), CCSpecial.class));
    }
}
