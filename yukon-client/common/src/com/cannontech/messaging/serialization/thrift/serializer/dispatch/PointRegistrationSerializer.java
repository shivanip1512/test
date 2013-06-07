package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.dispatch.PointRegistrationMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.PointRegistration;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class PointRegistrationSerializer extends
    ThriftInheritanceSerializer<PointRegistrationMessage, BaseMessage, PointRegistration, Message> {

    public PointRegistrationSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Class<PointRegistrationMessage> getTargetMessageClass() {
        return PointRegistrationMessage.class;
    }

    @Override
    protected PointRegistration createThrifEntityInstance(Message entityParent) {
        PointRegistration entity = new PointRegistration();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(PointRegistration entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected PointRegistrationMessage createMessageInstance() {
        return new PointRegistrationMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, PointRegistration entity, PointRegistrationMessage msg) {
        msg.setPointIds(new HashSet<Integer>(entity.get_pointList()));
        msg.setRegFlags(entity.get_regFlags());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, PointRegistrationMessage msg, PointRegistration entity) {
    	Set<Integer> pointIdSet = msg.getPointIds();
    	List<Integer> pointIdList = pointIdSet != null ? new ArrayList<Integer>(pointIdSet):  new ArrayList<Integer>();
    	
    	entity.set_pointList(pointIdList);
        entity.set_regFlags(msg.getRegFlags());
    }

}
