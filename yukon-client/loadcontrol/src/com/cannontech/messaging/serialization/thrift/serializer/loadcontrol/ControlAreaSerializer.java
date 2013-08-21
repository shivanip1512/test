package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.messages.LMControlAreaMsg;
import com.cannontech.loadcontrol.messages.LMMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMControlAreaItem;
import com.cannontech.messaging.serialization.thrift.generated.LMControlAreas;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class ControlAreaSerializer
    extends
    ThriftInheritanceSerializer<LMControlAreaMsg, LMMessage, LMControlAreas, com.cannontech.messaging.serialization.thrift.generated.LMMessage> {

    public ControlAreaSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMControlAreaMsg> getTargetMessageClass() {
        return LMControlAreaMsg.class;
    }

    @Override
    protected LMControlAreas
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMMessage entityParent) {
        LMControlAreas entity = new LMControlAreas();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMMessage
        getThriftEntityParent(LMControlAreas entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMControlAreaMsg createMessageInstance() {
        return new LMControlAreaMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMControlAreas entity,
                                                   LMControlAreaMsg msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setMsgInfoBitMask(entity.get_msgInfoBitMask());
        msg.setLMControlAreaVector(helper.convertToMessageList(entity.get_controlAreas(), LMControlArea.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMControlAreaMsg msg,
                                                   LMControlAreas entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        entity.set_msgInfoBitMask(msg.getMsgInfoBitMask());
        entity.set_controlAreas(helper.convertToEntityList(msg.getLMControlAreaVector(), LMControlAreaItem.class));
    }
}
