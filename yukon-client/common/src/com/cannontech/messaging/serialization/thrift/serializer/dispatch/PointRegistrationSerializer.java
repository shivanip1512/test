package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class PointRegistrationSerializer
    extends
    ThriftInheritanceSerializer<PointRegistration, Message, com.cannontech.messaging.serialization.thrift.generated.PointRegistration, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public PointRegistrationSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<PointRegistration> getTargetMessageClass() {
        return PointRegistration.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.PointRegistration
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.PointRegistration entity =
            new com.cannontech.messaging.serialization.thrift.generated.PointRegistration();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.PointRegistration entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected PointRegistration createMessageInstance() {
        return new PointRegistration();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.PointRegistration entity,
                                        PointRegistration msg) {
        msg.setPointIds(new HashSet<Integer>(entity.get_pointList()));
        msg.setRegFlags(entity.get_regFlags());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, PointRegistration msg,
                                        com.cannontech.messaging.serialization.thrift.generated.PointRegistration entity) {
        Set<Integer> pointIdSet = msg.getPointIds();
        List<Integer> pointIdList = pointIdSet != null ? new ArrayList<Integer>(pointIdSet) : new ArrayList<Integer>();

        entity.set_pointList(pointIdList);
        entity.set_regFlags(msg.getRegFlags());
    }

}
