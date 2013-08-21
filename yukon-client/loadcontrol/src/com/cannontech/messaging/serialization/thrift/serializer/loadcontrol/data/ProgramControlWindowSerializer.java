package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMProgramControlWindow;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;

public class ProgramControlWindowSerializer
    extends
    ThriftSerializer<LMProgramControlWindow, com.cannontech.messaging.serialization.thrift.generated.LMProgramControlWindow> {

    public ProgramControlWindowSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMProgramControlWindow> getTargetMessageClass() {
        return LMProgramControlWindow.class;
    }

    @Override
    protected LMProgramControlWindow createMessageInstance() {
        return new LMProgramControlWindow();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMProgramControlWindow
        createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.LMProgramControlWindow();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMProgramControlWindow entity,
                                        LMProgramControlWindow msg) {
        msg.setYukonID(entity.get_paoId());
        msg.setWindowNumber(entity.get_windowNumber());
        msg.setAvailableStartTime(entity.get_availableStartTime());
        msg.setAvailableStopTime(entity.get_availableStopTime());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory,
                                        LMProgramControlWindow msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMProgramControlWindow entity) {
        entity.set_paoId(msg.getYukonID());
        entity.set_windowNumber(msg.getWindowNumber());
        entity.set_availableStartTime(msg.getAvailableStartTime());
        entity.set_availableStopTime(msg.getAvailableStopTime());
    }
}
