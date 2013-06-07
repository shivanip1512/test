package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.messaging.message.loadcontrol.EnergyExchangeAcceptMessage;
import com.cannontech.messaging.message.loadcontrol.LmMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeAccept;
import com.cannontech.messaging.serialization.thrift.generated.LMMessage;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeAcceptSerializer extends
    ThriftInheritanceSerializer<EnergyExchangeAcceptMessage, LmMessage, LMEnergyExchangeAccept, LMMessage> {

    public EnergyExchangeAcceptSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<EnergyExchangeAcceptMessage> getTargetMessageClass() {
        return EnergyExchangeAcceptMessage.class;
    }

    @Override
    protected LMEnergyExchangeAccept createThrifEntityInstance(LMMessage entityParent) {
        LMEnergyExchangeAccept entity = new LMEnergyExchangeAccept();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMMessage getThriftEntityParent(LMEnergyExchangeAccept entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected EnergyExchangeAcceptMessage createMessageInstance() {
        return new EnergyExchangeAcceptMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMEnergyExchangeAccept entity, EnergyExchangeAcceptMessage msg) {
        msg.setYukonId(entity.get_paoId());
        msg.setOfferId(entity.get_offerId());
        msg.setRevisionNumber(entity.get_revisionNumber());
        msg.setAcceptStatus(entity.get_acceptStatus());
        msg.setIpAddressOfCustomer(entity.get_ipAddressOfAcceptUser());
        msg.setUserIdName(entity.get_userIdName());
        msg.setNameOfAcceptPerson(entity.get_nameOfAcceptPerson());
        msg.setEnergyExchangeNotes(entity.get_energyExchangeNotes());
        msg.setAmountCommitted((Double[]) entity.get_amountsCommitted().toArray());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, EnergyExchangeAcceptMessage msg, LMEnergyExchangeAccept entity) {
        entity.set_paoId(msg.getYukonId());
        entity.set_offerId(msg.getOfferId());
        entity.set_revisionNumber(msg.getRevisionNumber());
        entity.set_acceptStatus(msg.getAcceptStatus());
        entity.set_ipAddressOfAcceptUser(msg.getIpAddressOfCustomer());
        entity.set_userIdName(msg.getUserIdName());
        entity.set_nameOfAcceptPerson(msg.getNameOfAcceptPerson());
        entity.set_energyExchangeNotes(msg.getEnergyExchangeNotes());
        entity.set_amountsCommitted(ConverterHelper.toList(msg.getAmountCommitted()));
    }

}
