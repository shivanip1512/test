package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.cannontech.messaging.message.capcontrol.CommandMessage;
import com.cannontech.messaging.message.capcontrol.DynamicCommandMessage;
import com.cannontech.messaging.message.capcontrol.DynamicCommandMessage.DynamicCommandType;
import com.cannontech.messaging.message.capcontrol.DynamicCommandMessage.Parameter;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCCommand;
import com.cannontech.messaging.serialization.thrift.generated.CCDynamicCommand;

public class DynamicCommandSerializer extends
    ThriftInheritanceSerializer<DynamicCommandMessage, CommandMessage, CCDynamicCommand, CCCommand> {

    public DynamicCommandSerializer(String messageType, CommandSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<DynamicCommandMessage> getTargetMessageClass() {
        return DynamicCommandMessage.class;
    }

    @Override
    protected CCDynamicCommand createThrifEntityInstance(CCCommand entityParent) {
        CCDynamicCommand entity = new CCDynamicCommand();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCCommand getThriftEntityParent(CCDynamicCommand entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected DynamicCommandMessage createMessageInstance() {
        return new DynamicCommandMessage(DynamicCommandType.UNDEFINED);
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCDynamicCommand entity, DynamicCommandMessage msg) {
        msg.setCommandType(DynamicCommandType.values()[entity.get_commandType()]);

        for (Entry<Integer, Integer> intMapEntry : entity.get_longParameters().entrySet()) {
            msg.addParameter(Parameter.values()[intMapEntry.getKey()], intMapEntry.getValue());
        }

        for (Entry<Integer, Double> doubleMapEntry : entity.get_doubleParameters().entrySet()) {
            msg.addParameter(Parameter.values()[doubleMapEntry.getKey()], doubleMapEntry.getValue());
        }
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DynamicCommandMessage msg, CCDynamicCommand entity) {
        entity.set_commandType(msg.getCommandType().ordinal());

        Map<Integer, Integer> intMap = new HashMap<Integer, Integer>();
        for (Entry<Parameter, Integer> intMapEntry : msg.getIntParameters().entrySet()) {
            intMap.put(intMapEntry.getKey().getSerializeId(), intMapEntry.getValue());
        }

        Map<Integer, Double> doubleMap = new HashMap<Integer, Double>();
        for (Entry<Parameter, Double> doubleMapEntry : msg.getDoubleParameters().entrySet()) {
            doubleMap.put(doubleMapEntry.getKey().getSerializeId(), doubleMapEntry.getValue());
        }
    }
}
