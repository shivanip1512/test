package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.messaging.message.loadcontrol.EnergyExchangeControlMessage;
import com.cannontech.messaging.message.loadcontrol.LmMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeControl;
import com.cannontech.messaging.serialization.thrift.generated.LMMessage;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeControlSerializer extends
    ThriftInheritanceSerializer<EnergyExchangeControlMessage, LmMessage, LMEnergyExchangeControl, LMMessage> {

    public EnergyExchangeControlSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<EnergyExchangeControlMessage> getTargetMessageClass() {
        return EnergyExchangeControlMessage.class;
    }

    @Override
    protected LMEnergyExchangeControl createThrifEntityInstance(LMMessage entityParent) {
        LMEnergyExchangeControl entity = new LMEnergyExchangeControl();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMMessage getThriftEntityParent(LMEnergyExchangeControl entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected EnergyExchangeControlMessage createMessageInstance() {
        return new EnergyExchangeControlMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMEnergyExchangeControl entity, EnergyExchangeControlMessage msg) {
        msg.setCommand(entity.get_command());
        msg.setYukonId(entity.get_paoId());
        msg.setOfferId(entity.get_offerId());
        msg.setOfferDate(ConverterHelper.millisecToDate(entity.get_offerDate()));
        msg.setNotificationDateTime(ConverterHelper.millisecToDate(entity.get_notificationDatetime()));
        msg.setExpirationDateTime(ConverterHelper.millisecToDate(entity.get_expirationDatetime()));
        msg.setAdditionalInfo(entity.get_additionalInfo());
        msg.setAmountRequested((Double[]) entity.get_amountsRequested().toArray());
        msg.setPricesOffered((Integer[]) entity.get_pricesOffered().toArray());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, EnergyExchangeControlMessage msg, LMEnergyExchangeControl entity) {
        entity.set_command(msg.getCommand());
        entity.set_paoId(msg.getYukonId());
        entity.set_offerId(msg.getOfferId());
        entity.set_offerDate(ConverterHelper.dateToMillisec(msg.getOfferDate()));
        entity.set_notificationDatetime(ConverterHelper.dateToMillisec(msg.getNotificationDateTime()));
        entity.set_expirationDatetime(ConverterHelper.dateToMillisec(msg.getExpirationDateTime()));
        entity.set_additionalInfo(msg.getAdditionalInfo());
        entity.set_amountsRequested(ConverterHelper.toList(msg.getAmountRequested()));
        entity.set_pricesOffered(ConverterHelper.toList(msg.getPricesOffered()));
    }
}
