package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.GenericMessage;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ProgramDirectSerializer
    extends
    ThriftInheritanceSerializer<LMProgramDirect, LMProgramBase, com.cannontech.messaging.serialization.thrift.generated.LMProgramDirect, com.cannontech.messaging.serialization.thrift.generated.LMProgramBase> {

    public ProgramDirectSerializer(String messageType, ProgramSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMProgramDirect> getTargetMessageClass() {
        return LMProgramDirect.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMProgramDirect
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMProgramBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMProgramDirect entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMProgramDirect();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMProgramBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMProgramDirect entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMProgramDirect createMessageInstance() {
        return new LMProgramDirect();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMProgramDirect entity,
                                        LMProgramDirect msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        msg.setActiveMasterPrograms(helper.convertToMessageVector(entity.get_activeMasters(), LMProgramBase.class));

        msg.setActiveSubordinatePrograms(helper.convertToMessageVector(entity.get_activeSubordinates(), LMProgramBase.class));

        msg.setDirectGearVector(helper.convertToMessageList(entity.get_lmProgramDirectGears(),
                                                              LMProgramDirectGear.class));

        msg.setLoadControlGroupVector(helper.convertToMessageList(entity.get_lmProgramDirectGroups(), LMGroupBase.class));

        msg.setConstraintOverride(entity.is_constraintOverride());
        msg.setCurrentGearNumber(entity.get_currentGearNumber());
        msg.setDirectStartTime(ConverterHelper.millisecToCalendar(entity.get_directStartTime()));
        msg.setDirectStopTime(ConverterHelper.millisecToCalendar(entity.get_directstopTime()));
        msg.setLastGroupControlled(entity.get_lastGroupControlled());
        msg.setNotifyActiveTime(ConverterHelper.millisecToCalendar(entity.get_notifyActiveTime()));
        msg.setNotifyInactiveTime(ConverterHelper.millisecToCalendar(entity.get_notifyInactiveTime()));
        msg.setStartedRampingOut(ConverterHelper.millisecToCalendar(entity.get_startedRampingOut()));
        msg.setTriggerOffset(entity.get_triggerOffset());
        msg.setTriggerRestoreOffset(entity.get_triggerRestoreOffset());
        msg.setOriginSource(entity.get_originSource());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory factory, LMProgramDirect msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMProgramDirect entity) {
        throw new UnsupportedOperationException("Message serialization not supported");
    }

}
