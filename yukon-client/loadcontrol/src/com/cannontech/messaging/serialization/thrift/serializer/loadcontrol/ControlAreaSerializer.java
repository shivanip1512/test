package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.messaging.message.loadcontrol.ControlAreaMessage;
import com.cannontech.messaging.message.loadcontrol.LmMessage;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMControlAreaItem;
import com.cannontech.messaging.serialization.thrift.generated.LMControlAreas;
import com.cannontech.messaging.serialization.thrift.generated.LMMessage;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class ControlAreaSerializer extends
    ThriftInheritanceSerializer<ControlAreaMessage, LmMessage, LMControlAreas, LMMessage> {

    public ControlAreaSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ControlAreaMessage> getTargetMessageClass() {
        return ControlAreaMessage.class;
    }

    @Override
    protected LMControlAreas createThrifEntityInstance(LMMessage entityParent) {
        LMControlAreas entity = new LMControlAreas();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMMessage getThriftEntityParent(LMControlAreas entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ControlAreaMessage createMessageInstance() {
        return new ControlAreaMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMControlAreas entity, ControlAreaMessage msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setMsgInfoBitMask(entity.get_msgInfoBitMask());
        msg.setLMControlAreaVector(helper.convertToMessageList(entity.get_controlAreas(),
            ControlAreaItem.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ControlAreaMessage msg, LMControlAreas entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        entity.set_msgInfoBitMask(msg.getMsgInfoBitMask());
        entity.set_controlAreas(helper.convertToEntityList(msg.getLMControlAreaVector(),
            LMControlAreaItem.class));
    }
}
