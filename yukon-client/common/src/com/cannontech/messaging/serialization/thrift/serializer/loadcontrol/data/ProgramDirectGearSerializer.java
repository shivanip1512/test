package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.database.db.device.lm.GearControlMethod;

import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;


public class ProgramDirectGearSerializer extends ThriftSerializer<LMProgramDirectGear, com.cannontech.messaging.serialization.thrift.generated.LMProgramDirectGear> {

    protected ProgramDirectGearSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMProgramDirectGear> getTargetMessageClass() {
        return LMProgramDirectGear.class;
    }

    @Override
    protected LMProgramDirectGear createMessageInstance() {
        return new LMProgramDirectGear();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMProgramDirectGear createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.LMProgramDirectGear();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, com.cannontech.messaging.serialization.thrift.generated.LMProgramDirectGear entity,
                                                   LMProgramDirectGear msg) {
        msg.setChangeCondition(entity.get_changeCondition());
        msg.setChangeDuration(entity.get_changeDuration());
        msg.setChangePriority(entity.get_changePriority());
        msg.setChangeTriggerNumber(entity.get_changeTriggerNumber());
        msg.setChangeTriggerOffset(entity.get_changeTriggerOffset());
        msg.setControlMethod(GearControlMethod.getGearControlMethod(entity.get_controlMethod()));
        msg.setCycleRefreshRate(entity.get_cyclereFreshRate());
        msg.setGearName(entity.get_gearName());
        msg.setGearNumber(entity.get_gearNumber());
        msg.setGroupSelectionMethod(entity.get_groupSelectionMethod());
        msg.setKwReduction(entity.get_kwReduction());
        msg.setMethodOptionMax(entity.get_methodOptionMax());
        msg.setMethodOptionType(entity.get_methodOptionType());
        msg.setMethodPeriod(entity.get_methodPeriod());
        msg.setMethodRate(entity.get_methodRate());
        msg.setMethodRateCount(entity.get_methodRateCount());
        msg.setMethodStopType(entity.get_methodStopType());
        msg.setPercentReduction(entity.get_percentReduction());
        msg.setRampInInterval(entity.get_rampInInterval());
        msg.setRampInPercent(entity.get_rampInPercent());
        msg.setRampOutInterval(entity.get_rampOutInterval());
        msg.setRampOutPercent(entity.get_rampOutPercent());
        msg.setProgramId(entity.get_programPaoId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, LMProgramDirectGear msg,
                                                   com.cannontech.messaging.serialization.thrift.generated.LMProgramDirectGear entity) {
       throw new UnsupportedOperationException("Message serialization not supported");
    }

}
