package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.CCPao;

public class StreamableSerializer extends ThriftSerializer<StreamableCapObject, CCPao> {

    protected StreamableSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<StreamableCapObject> getTargetMessageClass() {
        return StreamableCapObject.class;
    }

    @Override
    protected StreamableCapObject createMessageInstance() {
        throw new RuntimeException("Can not create StreamableCapObject object, class is abstract");
    }

    @Override
    protected CCPao createThriftEntityInstance() {
        return new CCPao();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCPao entity,
                                                   StreamableCapObject msg) {
        msg.setCcArea(entity.get_paoDescription());
        msg.setCcCategory(entity.get_paoCategory());
        msg.setCcClass(entity.get_paoClass());
        msg.setCcDisableFlag(entity.is_disableFlag());
        msg.setCcId(entity.get_paoId());
        msg.setCcName(entity.get_paoName());
        msg.setCcType(entity.get_paoType());

        // For legacy reasons, the parentId is not defined at this level in the Thrift Entity.
        // It is therefore the responsibility of the child to populate this field when needed.
        // msg.setParentId(entity.get_paoParentId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, StreamableCapObject msg,
                                                   CCPao entity) {
        entity.set_disableFlag(msg.getCcDisableFlag());
        entity.set_paoCategory(msg.getCcCategory());
        entity.set_paoClass(msg.getCcClass());
        entity.set_paoDescription(msg.getCcArea());
        entity.set_paoId(msg.getCcId());
        entity.set_paoName(msg.getCcName());
        entity.set_paoType(msg.getCcType());

        // For legacy reasons, the parentId is not defined at this level in the Thrift Entity.
        // It is therefore the responsibility of the child to populate this field when needed.
        // entity.set_paoParentId(msg.getParentId());
    }
}
