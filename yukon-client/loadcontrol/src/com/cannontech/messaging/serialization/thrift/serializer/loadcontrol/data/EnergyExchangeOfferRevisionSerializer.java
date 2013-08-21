package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeOfferRevisionSerializer
    extends
    ThriftSerializer<LMEnergyExchangeOfferRevision, com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOfferRevision> {

    protected EnergyExchangeOfferRevisionSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMEnergyExchangeOfferRevision> getTargetMessageClass() {
        return LMEnergyExchangeOfferRevision.class;
    }

    @Override
    protected LMEnergyExchangeOfferRevision createMessageInstance() {
        return new LMEnergyExchangeOfferRevision();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOfferRevision
        createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOfferRevision();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOfferRevision entity,
                                        LMEnergyExchangeOfferRevision msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        msg.setActionDateTime(ConverterHelper.millisecToDate(entity.get_actionDatetime()));
        msg.setAdditionalInfo(entity.get_additionalInfo());
        msg.setEnergyExchangeHourlyOffers(helper.convertToMessageVector(entity.get_lmEnergyExchangeHourlyOffers(),
                                                                        LMEnergyExchangeHourlyOffer.class));
        msg.setNotificationDateTime(ConverterHelper.millisecToDate(entity.get_notificationDatetime()));
        msg.setOfferExpirationDateTime(ConverterHelper.millisecToDate(entity.get_offerexpirationDatetime()));
        msg.setOfferID(entity.get_offerId());
        msg.setRevisionNumber(entity.get_revisionNumber());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                        LMEnergyExchangeOfferRevision msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOfferRevision entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        entity.set_actionDatetime(ConverterHelper.dateToMillisec(msg.getActionDateTime()));
        entity.set_additionalInfo(msg.getAdditionalInfo());
        entity
            .set_lmEnergyExchangeHourlyOffers(helper.convertToEntityList(msg.getEnergyExchangeHourlyOffers(),
                                                                         com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyOffer.class));
        entity.set_notificationDatetime(ConverterHelper.dateToMillisec(msg.getNotificationDateTime()));
        entity.set_offerexpirationDatetime(ConverterHelper.dateToMillisec(msg.getOfferExpirationDateTime()));
        entity.set_offerId(msg.getOfferID());
        entity.set_revisionNumber(msg.getRevisionNumber());
    }

}
