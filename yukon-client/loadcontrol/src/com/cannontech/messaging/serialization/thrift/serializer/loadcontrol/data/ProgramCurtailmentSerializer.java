package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import java.util.List;

import com.cannontech.messaging.message.loadcontrol.data.CurtailCustomer;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.messaging.message.loadcontrol.data.ProgramCurtailment;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMProgramBase;
import com.cannontech.messaging.serialization.thrift.generated.LMProgramCurtailment;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ProgramCurtailmentSerializer extends
    ThriftInheritanceSerializer<ProgramCurtailment, Program, LMProgramCurtailment, LMProgramBase> {

    public ProgramCurtailmentSerializer(String messageType, ProgramSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ProgramCurtailment> getTargetMessageClass() {
        return ProgramCurtailment.class;
    }

    @Override
    protected LMProgramCurtailment createThrifEntityInstance(LMProgramBase entityParent) {
        LMProgramCurtailment entity = new LMProgramCurtailment();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMProgramBase getThriftEntityParent(LMProgramCurtailment entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ProgramCurtailment createMessageInstance() {
        return new ProgramCurtailment();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMProgramCurtailment entity,
                                                   ProgramCurtailment msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setMinNotifyTime(entity.get_minNotifyTime());
        msg.setHeading(entity.get_heading());
        msg.setMessageHeader(entity.get_messageHeader());
        msg.setMessageFooter(entity.get_messageFooter());
        msg.setAckTimeLimit(entity.get_acktimeLimit());
        msg.setCanceledMsg(entity.get_canceledMsg());
        msg.setStoppedEarlyMsg(entity.get_stoppedEarlyMsg());
        msg.setCurtailReferenceId(entity.get_curtailReferenceId());
        msg.setActionDateTime(ConverterHelper.millisecToCalendar(entity.get_actionDateTime()));
        msg.setNotificationDateTime(ConverterHelper.millisecToCalendar(entity.get_notificationDateTime()));
        msg.setCurtailmentStartTime(ConverterHelper.millisecToCalendar(entity.get_curtailmentStartTime()));
        msg.setCurtailmentStopTime(ConverterHelper.millisecToCalendar(entity.get_curtailmentStopTime()));
        msg.setRunStatus(entity.get_runStatus());
        msg.setAdditionalInfo(entity.get_additionalInfo());
        msg.setAckTimeLimit(entity.get_acktimeLimit());

        // This looks bad but the legacy code actually expects to see <CurtailCustomer> list being passed in the
        // <GroupeBase> list on special occasions.
        // The code is constructed around this particular case and explicit type checking is used where needed.
        // Moreover, correcting this "type hack" would require rework of the LoadControler module and it has bee decided
        // to live with it for the time being.
        List CustomerList =
            helper.convertToMessageList(entity.get_lmProgramCurtailmentCustomers(), CurtailCustomer.class);
        msg.setLoadControlGroupVector(CustomerList);

    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ProgramCurtailment msg,
                                                   LMProgramCurtailment entity) {
        // not implemented
    }

}
