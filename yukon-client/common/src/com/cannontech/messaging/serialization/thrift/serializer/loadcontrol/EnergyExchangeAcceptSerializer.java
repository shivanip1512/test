package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.loadcontrol.messages.LMEnergyExchangeAcceptMsg;
import com.cannontech.loadcontrol.messages.LMMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeAccept;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeAcceptSerializer
    extends
    ThriftInheritanceSerializer<LMEnergyExchangeAcceptMsg, LMMessage, LMEnergyExchangeAccept, com.cannontech.messaging.serialization.thrift.generated.LMMessage> {

    public EnergyExchangeAcceptSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMEnergyExchangeAcceptMsg> getTargetMessageClass() {
        return LMEnergyExchangeAcceptMsg.class;
    }

    @Override
    protected LMEnergyExchangeAccept
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMMessage entityParent) {
        LMEnergyExchangeAccept entity = new LMEnergyExchangeAccept();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMMessage
        getThriftEntityParent(LMEnergyExchangeAccept entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMEnergyExchangeAcceptMsg createMessageInstance() {
        return new LMEnergyExchangeAcceptMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMEnergyExchangeAccept entity,
                                                   LMEnergyExchangeAcceptMsg msg) {
        msg.setYukonID(entity.get_paoId());
        msg.setOfferID(entity.get_offerId());
        msg.setRevisionNumber(entity.get_revisionNumber());
        msg.setAcceptStatus(entity.get_acceptStatus());
        msg.setIpAddressOfCustomer(entity.get_ipAddressOfAcceptUser());
        msg.setUserIDName(entity.get_userIdName());
        msg.setNameOfAcceptPerson(entity.get_nameOfAcceptPerson());
        msg.setEnergyExchangeNotes(entity.get_energyExchangeNotes());
        msg.setAmountCommitted(ConverterHelper.toArray(entity.get_amountsCommitted(), Double.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMEnergyExchangeAcceptMsg msg,
                                                   LMEnergyExchangeAccept entity) {
        entity.set_paoId(msg.getYukonID());
        entity.set_offerId(msg.getOfferID());
        entity.set_revisionNumber(msg.getRevisionNumber());
        entity.set_acceptStatus(msg.getAcceptStatus());
        entity.set_ipAddressOfAcceptUser(msg.getIpAddressOfCustomer());
        entity.set_userIdName(msg.getUserIDName());
        entity.set_nameOfAcceptPerson(msg.getNameOfAcceptPerson());
        entity.set_energyExchangeNotes(msg.getEnergyExchangeNotes());
        entity.set_amountsCommitted(ConverterHelper.toList(msg.getAmountCommitted()));
    }

}
