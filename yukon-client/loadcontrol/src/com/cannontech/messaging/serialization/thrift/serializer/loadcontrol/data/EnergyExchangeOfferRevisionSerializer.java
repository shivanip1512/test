package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeHourlyOffer;
import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeOfferRevision;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyOffer;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOfferRevision;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeOfferRevisionSerializer extends
    ThriftSerializer<EnergyExchangeOfferRevision, LMEnergyExchangeOfferRevision> {

    protected EnergyExchangeOfferRevisionSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<EnergyExchangeOfferRevision> getTargetMessageClass() {
        return EnergyExchangeOfferRevision.class;
    }

    @Override
    protected EnergyExchangeOfferRevision createMessageInstance() {
        return new EnergyExchangeOfferRevision();
    }

    @Override
    protected LMEnergyExchangeOfferRevision createThrifEntityInstance() {
        return new LMEnergyExchangeOfferRevision();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMEnergyExchangeOfferRevision entity,
                                                   EnergyExchangeOfferRevision msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        msg.setActionDateTime(ConverterHelper.millisecToDate(entity.get_actionDatetime()));
        msg.setAdditionalInfo(entity.get_additionalInfo());
        msg.setEnergyExchangeHourlyOffers(helper.convertToMessageVector(entity.get_lmEnergyExchangeHourlyOffers(),
                                                                        EnergyExchangeHourlyOffer.class));
        msg.setNotificationDateTime(ConverterHelper.millisecToDate(entity.get_notificationDatetime()));
        msg.setOfferExpirationDateTime(ConverterHelper.millisecToDate(entity.get_offerexpirationDatetime()));
        msg.setOfferId(entity.get_offerId());
        msg.setRevisionNumber(entity.get_revisionNumber());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, EnergyExchangeOfferRevision msg,
                                                   LMEnergyExchangeOfferRevision entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        entity.set_actionDatetime(ConverterHelper.dateToMillisec(msg.getActionDateTime()));
        entity.set_additionalInfo(msg.getAdditionalInfo());
        entity.set_lmEnergyExchangeHourlyOffers(helper.convertToEntityList(msg.getEnergyExchangeHourlyOffers(),
                                                                           LMEnergyExchangeHourlyOffer.class));
        entity.set_notificationDatetime(ConverterHelper.dateToMillisec(msg.getNotificationDateTime()));
        entity.set_offerexpirationDatetime(ConverterHelper.dateToMillisec(msg.getOfferExpirationDateTime()));
        entity.set_offerId(msg.getOfferId());
        entity.set_revisionNumber(msg.getRevisionNumber());
    }

}
