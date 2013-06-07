package com.cannontech.messaging.serialization.thrift.serializer.capcontrol.streamable;

import com.cannontech.capcontrol.TapOperation;
import com.cannontech.messaging.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCPao;
import com.cannontech.messaging.serialization.thrift.generated.CCVoltageRegulatorItem;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class VoltageRegulatorItemSerializer extends
    ThriftInheritanceSerializer<VoltageRegulatorFlags, StreamableCapObject, CCVoltageRegulatorItem, CCPao> {

    public VoltageRegulatorItemSerializer(String messageType, StreamableSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<VoltageRegulatorFlags> getTargetMessageClass() {
        return VoltageRegulatorFlags.class;
    }

    @Override
    protected CCVoltageRegulatorItem createThrifEntityInstance(CCPao entityParent) {
        CCVoltageRegulatorItem entity = new CCVoltageRegulatorItem();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCPao getThriftEntityParent(CCVoltageRegulatorItem entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected VoltageRegulatorFlags createMessageInstance() {
        return new VoltageRegulatorFlags();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCVoltageRegulatorItem entity, VoltageRegulatorFlags msg) {
        msg.setParentId(entity.get_parentId());
        msg.setLastOperation(TapOperation.getForTapOperationId(entity.get_lastTapOperation()));
        msg.setLastOperationTime(ConverterHelper.millisecToDate(entity.get_lastTapOperationTime()));

        msg.setRecentOperation(entity.is_recentTapOperation());
        msg.setAutoRemote(entity.get_lastOperatingMode() == 1);
        msg.setAutoRemoteManual(entity.get_lastCommandedOperatingMode() == 1);
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, VoltageRegulatorFlags msg, CCVoltageRegulatorItem entity) {
        throw new RuntimeException("This operation is not implemented");
    }

}
