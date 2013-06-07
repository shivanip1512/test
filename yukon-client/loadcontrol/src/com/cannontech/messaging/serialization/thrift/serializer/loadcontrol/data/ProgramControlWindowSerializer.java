package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.ProgramControlWindow;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMProgramControlWindow;

public class ProgramControlWindowSerializer extends ThriftSerializer<ProgramControlWindow, LMProgramControlWindow> {

    public ProgramControlWindowSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<ProgramControlWindow> getTargetMessageClass() {
        return ProgramControlWindow.class;
    }

    @Override
    protected ProgramControlWindow createMessageInstance() {
        return new ProgramControlWindow();
    }

    @Override
    protected LMProgramControlWindow createThrifEntityInstance() {
        return new LMProgramControlWindow();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMProgramControlWindow entity, ProgramControlWindow msg) {
        msg.setYukonId(entity.get_paoId());
        msg.setWindowNumber(entity.get_windowNumber());
        msg.setAvailableStartTime((int) entity.get_availableStartTime());
        msg.setAvailableStopTime((int) entity.get_availableStopTime());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ProgramControlWindow msg, LMProgramControlWindow entity) {
        entity.set_paoId(msg.getYukonId());
        entity.set_windowNumber(msg.getWindowNumber());
        entity.set_availableStartTime(msg.getAvailableStartTime());
        entity.set_availableStopTime(msg.getAvailableStopTime());
    }
}
