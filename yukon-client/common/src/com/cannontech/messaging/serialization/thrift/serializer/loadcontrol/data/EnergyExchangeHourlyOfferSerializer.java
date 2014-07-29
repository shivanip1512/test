package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;

public class EnergyExchangeHourlyOfferSerializer
    extends
    ThriftSerializer<LMEnergyExchangeHourlyOffer, com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyOffer> {

    protected EnergyExchangeHourlyOfferSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMEnergyExchangeHourlyOffer> getTargetMessageClass() {
        return LMEnergyExchangeHourlyOffer.class;
    }

    @Override
    protected LMEnergyExchangeHourlyOffer createMessageInstance() {
        return new LMEnergyExchangeHourlyOffer();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyOffer
        createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyOffer();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyOffer entity,
                                        LMEnergyExchangeHourlyOffer msg) {
        msg.setAmountRequested(entity.get_amountRequested());
        msg.setHour(entity.get_hour());
        msg.setOfferID(entity.get_offerId());
        msg.setPrice(entity.get_price());
        msg.setRevisionNumber(entity.get_revisionNumber());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                        LMEnergyExchangeHourlyOffer msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyOffer entity) {
        entity.set_amountRequested(msg.getAmountRequested());
        entity.set_hour(msg.getHour());
        entity.set_offerId(msg.getOfferID());
        entity.set_price(msg.getPrice());
        entity.set_revisionNumber(msg.getRevisionNumber());
    }
}
