package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeHourlyOffer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyOffer;

public class EnergyExchangeHourlyOfferSerializer extends
    ThriftSerializer<EnergyExchangeHourlyOffer, LMEnergyExchangeHourlyOffer> {

    protected EnergyExchangeHourlyOfferSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<EnergyExchangeHourlyOffer> getTargetMessageClass() {
        return EnergyExchangeHourlyOffer.class;
    }

    @Override
    protected EnergyExchangeHourlyOffer createMessageInstance() {
        return new EnergyExchangeHourlyOffer();
    }

    @Override
    protected LMEnergyExchangeHourlyOffer createThrifEntityInstance() {
        return new LMEnergyExchangeHourlyOffer();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMEnergyExchangeHourlyOffer entity,
                                                   EnergyExchangeHourlyOffer msg) {
        msg.setAmountRequested(entity.get_amountRequested());
        msg.setHour(entity.get_hour());
        msg.setOfferId(entity.get_offerId());
        msg.setPrice(entity.get_price());
        msg.setRevisionNumber(entity.get_revisionNumber());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, EnergyExchangeHourlyOffer msg,
                                                   LMEnergyExchangeHourlyOffer entity) {
        entity.set_amountRequested(msg.getAmountRequested());
        entity.set_hour(msg.getHour());
        entity.set_offerId(msg.getOfferId());
        entity.set_price(msg.getPrice());
        entity.set_revisionNumber(msg.getRevisionNumber());
    }
}
