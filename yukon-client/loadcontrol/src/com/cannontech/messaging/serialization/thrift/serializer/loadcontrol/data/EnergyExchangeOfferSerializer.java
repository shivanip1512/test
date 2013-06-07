package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeOffer;
import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeOfferRevision;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOffer;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOfferRevision;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeOfferSerializer extends ThriftSerializer<EnergyExchangeOffer, LMEnergyExchangeOffer> {

    protected EnergyExchangeOfferSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<EnergyExchangeOffer> getTargetMessageClass() {
        return EnergyExchangeOffer.class;
    }

    @Override
    protected EnergyExchangeOffer createMessageInstance() {
        return new EnergyExchangeOffer();
    }

    @Override
    protected LMEnergyExchangeOffer createThrifEntityInstance() {
        return new LMEnergyExchangeOffer();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMEnergyExchangeOffer entity,
                                                   EnergyExchangeOffer msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        msg.setEnergyExchangeOfferRevisions(helper.convertToMessageVector(entity.get_lmEnergyExchangeOfferRevisions(),
                                                                          EnergyExchangeOfferRevision.class));
        msg.setOfferDate(ConverterHelper.millisecToDate(entity.get_offerDate()));
        msg.setOfferId(entity.get_offerId());
        msg.setRunStatus(entity.get_runStatus());
        msg.setYukonID(entity.get_paoId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, EnergyExchangeOffer msg,
                                                   LMEnergyExchangeOffer entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        entity.set_lmEnergyExchangeOfferRevisions(helper.convertToEntityList(msg.getEnergyExchangeOfferRevisions(),
                                                                             LMEnergyExchangeOfferRevision.class));
        entity.set_offerDate(ConverterHelper.dateToMillisec(msg.getOfferDate()));
        entity.set_offerId(msg.getOfferId());
        entity.set_paoId(msg.getYukonId());
        entity.set_runStatus(msg.getRunStatus());
    }

}
