package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.messaging.message.loadcontrol.data.GroupBase;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.messaging.message.loadcontrol.data.ProgramDirect;
import com.cannontech.messaging.message.loadcontrol.data.ProgramDirectGear;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.GenericMessage;
import com.cannontech.messaging.serialization.thrift.generated.LMProgramBase;
import com.cannontech.messaging.serialization.thrift.generated.LMProgramDirect;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ProgramDirectSerializer extends
    ThriftInheritanceSerializer<ProgramDirect, Program, LMProgramDirect, LMProgramBase> {

    public ProgramDirectSerializer(String messageType, ProgramSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ProgramDirect> getTargetMessageClass() {
        return ProgramDirect.class;
    }

    @Override
    protected LMProgramDirect createThrifEntityInstance(LMProgramBase entityParent) {
        LMProgramDirect entity = new LMProgramDirect();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMProgramBase getThriftEntityParent(LMProgramDirect entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ProgramDirect createMessageInstance() {
        return new ProgramDirect();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMProgramDirect entity,
                                                   ProgramDirect msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        Vector<Object> msgVect = new Vector<Object>();

        for (GenericMessage innerMsg : entity.get_activeMasters()) {
            msgVect.add(helper.convertFromGeneric(innerMsg));
        }
        msg.setActiveMasterPrograms(msgVect);

        msgVect.clear();
        for (GenericMessage innerMsg : entity.get_activeSubordinates()) {
            msgVect.add(helper.convertFromGeneric(innerMsg));
        }
        msg.setActiveSubordinatePrograms(msgVect);

        msg.setDirectGearVector(helper.convertToMessageVector(entity.get_lmProgramDirectGears(),
                                                              ProgramDirectGear.class));

        List<GroupBase> msgList = new ArrayList<GroupBase>();
        for (GenericMessage innerMsg : entity.get_lmProgramDirectGroups()) {
            msgList.add((GroupBase) helper.convertFromGeneric(innerMsg));
        }

        msg.setLoadControlGroupVector(msgList);

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
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, ProgramDirect msg,
                                                   LMProgramDirect entity) {
       throw new RuntimeException("Not implemented");       
    }

}
