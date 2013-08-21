package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.DynamicCommand;
import com.cannontech.message.capcontrol.model.DynamicCommand.DynamicCommandType;
import com.cannontech.message.capcontrol.model.DynamicCommand.Parameter;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCCommand;
import com.cannontech.messaging.serialization.thrift.generated.CCDynamicCommand;

public class DynamicCommandSerializer extends
    ThriftInheritanceSerializer<DynamicCommand, CapControlCommand, CCDynamicCommand, CCCommand> {

    public DynamicCommandSerializer(String messageType, CommandSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<DynamicCommand> getTargetMessageClass() {
        return DynamicCommand.class;
    }

    @Override
    protected CCDynamicCommand createThriftEntityInstance(CCCommand entityParent) {
        CCDynamicCommand entity = new CCDynamicCommand();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCCommand getThriftEntityParent(CCDynamicCommand entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected DynamicCommand createMessageInstance() {
        return new DynamicCommand(DynamicCommandType.UNDEFINED);
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCDynamicCommand entity,
                                                   DynamicCommand msg) {
        msg.setCommandType(DynamicCommandType.values()[entity.get_commandType()]);

        for (Entry<Integer, Integer> intMapEntry : entity.get_longParameters().entrySet()) {
            msg.addParameter(Parameter.values()[intMapEntry.getKey()], intMapEntry.getValue());
        }

        for (Entry<Integer, Double> doubleMapEntry : entity.get_doubleParameters().entrySet()) {
            msg.addParameter(Parameter.values()[doubleMapEntry.getKey()], doubleMapEntry.getValue());
        }
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DynamicCommand msg,
                                                   CCDynamicCommand entity) {
        entity.set_commandType(msg.getCommandType().getSerializeId());

        Map<Integer, Integer> intMap = new HashMap<Integer, Integer>();
        for (Entry<Parameter, Integer> intMapEntry : msg.getIntParameters().entrySet()) {
            intMap.put(intMapEntry.getKey().getSerializeId(), intMapEntry.getValue());            
        }
        entity.set_longParameters(intMap);

        Map<Integer, Double> doubleMap = new HashMap<Integer, Double>();
        for (Entry<Parameter, Double> doubleMapEntry : msg.getDoubleParameters().entrySet()) {
            doubleMap.put(doubleMapEntry.getKey().getSerializeId(), doubleMapEntry.getValue());
        }
        entity.set_doubleParameters(doubleMap);
    }
}
