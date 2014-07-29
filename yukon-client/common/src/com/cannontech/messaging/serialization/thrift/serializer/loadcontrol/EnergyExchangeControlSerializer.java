package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg;
import com.cannontech.loadcontrol.messages.LMMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeControl;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeControlSerializer
    extends
    ThriftInheritanceSerializer<LMEnergyExchangeControlMsg, LMMessage, LMEnergyExchangeControl, com.cannontech.messaging.serialization.thrift.generated.LMMessage> {

    public EnergyExchangeControlSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMEnergyExchangeControlMsg> getTargetMessageClass() {
        return LMEnergyExchangeControlMsg.class;
    }

    @Override
    protected LMEnergyExchangeControl
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMMessage entityParent) {
        LMEnergyExchangeControl entity = new LMEnergyExchangeControl();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMMessage
        getThriftEntityParent(LMEnergyExchangeControl entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMEnergyExchangeControlMsg createMessageInstance() {
        return new LMEnergyExchangeControlMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMEnergyExchangeControl entity,
                                                   LMEnergyExchangeControlMsg msg) {
        msg.setCommand(entity.get_command());
        msg.setYukonID(entity.get_paoId());
        msg.setOfferID(entity.get_offerId());
        msg.setOfferDate(ConverterHelper.millisecToDate(entity.get_offerDate()));
        msg.setNotificationDateTime(ConverterHelper.millisecToDate(entity.get_notificationDatetime()));
        msg.setExpirationDateTime(ConverterHelper.millisecToDate(entity.get_expirationDatetime()));
        msg.setAdditionalInfo(entity.get_additionalInfo());
        msg.setAmountRequested(ConverterHelper.toArray(entity.get_amountsRequested(), Double.class));
        msg.setPricesOffered(ConverterHelper.toArray(entity.get_pricesOffered(), Integer.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMEnergyExchangeControlMsg msg,
                                                   LMEnergyExchangeControl entity) {
        entity.set_command(msg.getCommand());
        entity.set_paoId(msg.getYukonID());
        entity.set_offerId(msg.getOfferID());
        entity.set_offerDate(ConverterHelper.dateToMillisec(msg.getOfferDate()));
        entity.set_notificationDatetime(ConverterHelper.dateToMillisec(msg.getNotificationDateTime()));
        entity.set_expirationDatetime(ConverterHelper.dateToMillisec(msg.getExpirationDateTime()));
        entity.set_additionalInfo(msg.getAdditionalInfo());
        entity.set_amountsRequested(ConverterHelper.toList(msg.getAmountRequested()));
        entity.set_pricesOffered(ConverterHelper.toList(msg.getPricesOffered()));
    }
}
