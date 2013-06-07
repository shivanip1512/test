package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.CiCustomerBase;
import com.cannontech.messaging.message.loadcontrol.data.CurtailCustomer;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase;
import com.cannontech.messaging.serialization.thrift.generated.LMCurtailCustomer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class CurtailCustomerSerializer extends
    ThriftInheritanceSerializer<CurtailCustomer, CiCustomerBase, LMCurtailCustomer, LMCICustomerBase> {

    public CurtailCustomerSerializer(String messageType, CiCustomerBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CurtailCustomer> getTargetMessageClass() {
        return CurtailCustomer.class;
    }

    @Override
    protected LMCurtailCustomer createThrifEntityInstance(LMCICustomerBase entityParent) {
        LMCurtailCustomer entity = new LMCurtailCustomer();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMCICustomerBase getThriftEntityParent(LMCurtailCustomer entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CurtailCustomer createMessageInstance() {
        return new CurtailCustomer();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMCurtailCustomer entity, CurtailCustomer msg) {
        msg.setAckDateTime(ConverterHelper.millisecToDate(entity.get_ackDatetime()));
        msg.setAckLateFlag(entity.is_ackLateFlag());
        msg.setAckStatus(entity.get_acknowledgeStatus());
        msg.setCurtailmentNotes(entity.get_curtailmentNotes());
        msg.setCurtailRefId(entity.get_curtailReferenceId());
        msg.setIpAddress(entity.get_ipAddressOfAckUser());
        msg.setRequireAck(entity.is_requireAck());
        msg.setUserIDname(entity.get_userIdName());
        msg.setNameOfAckPerson(entity.get_nameOfAckPerson());

    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CurtailCustomer msg, LMCurtailCustomer entity) {
        entity.set_ackDatetime(ConverterHelper.dateToMillisec(msg.getAckDateTime()));
        entity.set_ackLateFlag(msg.getAckLateFlag());
        entity.set_acknowledgeStatus(msg.getAckStatus());
        entity.set_curtailmentNotes(msg.getCurtailmentNotes());
        entity.set_curtailReferenceId(ConverterHelper.UnsignedToInt(msg.getCurtailRefId()));
        entity.set_ipAddressOfAckUser(msg.getIpAddress());
        entity.set_nameOfAckPerson(msg.getNameOfAckPerson());
        entity.set_requireAck(msg.getRequireAck());
        entity.set_userIdName(msg.getUserIDname());
    }
}
