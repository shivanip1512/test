package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMCICustomerBase;
import com.cannontech.loadcontrol.data.LMCurtailCustomer;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class CurtailCustomerSerializer
    extends
    ThriftInheritanceSerializer<LMCurtailCustomer, LMCICustomerBase, com.cannontech.messaging.serialization.thrift.generated.LMCurtailCustomer, com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase> {

    public CurtailCustomerSerializer(String messageType, CiCustomerBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMCurtailCustomer> getTargetMessageClass() {
        return LMCurtailCustomer.class;
    }

    @Override
    protected
        com.cannontech.messaging.serialization.thrift.generated.LMCurtailCustomer
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMCurtailCustomer entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMCurtailCustomer();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMCurtailCustomer entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMCurtailCustomer createMessageInstance() {
        return new LMCurtailCustomer();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMCurtailCustomer entity,
                                        LMCurtailCustomer msg) {
        msg.setAckDateTime(ConverterHelper.millisecToDate(entity.get_ackDatetime()));
        msg.setAckLateFlag(entity.is_ackLateFlag());
        msg.setAckStatus(entity.get_acknowledgeStatus());
        msg.setCurtailmentNotes(entity.get_curtailmentNotes());
        msg.setCurtailRefID((long)entity.get_curtailReferenceId());
        msg.setIpAddress(entity.get_ipAddressOfAckUser());
        msg.setRequireAck(entity.is_requireAck());
        msg.setUserIDname(entity.get_userIdName());
        msg.setNameOfAckPerson(entity.get_nameOfAckPerson());

    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMCurtailCustomer msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMCurtailCustomer entity) {
        entity.set_ackDatetime(ConverterHelper.dateToMillisec(msg.getAckDateTime()));
        entity.set_ackLateFlag(msg.getAckLateFlag());
        entity.set_acknowledgeStatus(msg.getAckStatus());
        entity.set_curtailmentNotes(msg.getCurtailmentNotes());
        entity.set_curtailReferenceId(msg.getCurtailRefID().intValue());
        entity.set_ipAddressOfAckUser(msg.getIpAddress());
        entity.set_nameOfAckPerson(msg.getNameOfAckPerson());
        entity.set_requireAck(msg.getRequireAck());
        entity.set_userIdName(msg.getUserIDname());
    }
}
