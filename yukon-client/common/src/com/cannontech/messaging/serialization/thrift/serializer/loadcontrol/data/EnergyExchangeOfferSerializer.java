package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeOffer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeOfferSerializer
    extends
    ThriftSerializer<LMEnergyExchangeOffer, com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOffer> {

    protected EnergyExchangeOfferSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMEnergyExchangeOffer> getTargetMessageClass() {
        return LMEnergyExchangeOffer.class;
    }

    @Override
    protected LMEnergyExchangeOffer createMessageInstance() {
        return new LMEnergyExchangeOffer();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOffer createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOffer();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOffer entity,
                                        LMEnergyExchangeOffer msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        msg.setEnergyExchangeOfferRevisions(helper.convertToMessageVector(entity.get_lmEnergyExchangeOfferRevisions(),
                                                                          LMEnergyExchangeOfferRevision.class));
        msg.setOfferDate(ConverterHelper.millisecToDate(entity.get_offerDate()));
        msg.setOfferID(entity.get_offerId());
        msg.setRunStatus(entity.get_runStatus());
        msg.setYukonID(entity.get_paoId());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                        LMEnergyExchangeOffer msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOffer entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        entity
            .set_lmEnergyExchangeOfferRevisions(helper.convertToEntityList(msg.getEnergyExchangeOfferRevisions(),
                                                                           com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOfferRevision.class));
        entity.set_offerDate(ConverterHelper.dateToMillisec(msg.getOfferDate()));
        entity.set_offerId(msg.getOfferID());
        entity.set_paoId(msg.getYukonID());
        entity.set_runStatus(msg.getRunStatus());
    }

}
