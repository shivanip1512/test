package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply;
import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeCustomerReplySerializer
    extends
    ThriftSerializer<LMEnergyExchangeCustomerReply, com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomerReply> {

    protected EnergyExchangeCustomerReplySerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMEnergyExchangeCustomerReply> getTargetMessageClass() {
        return LMEnergyExchangeCustomerReply.class;
    }

    @Override
    protected LMEnergyExchangeCustomerReply createMessageInstance() {
        return new LMEnergyExchangeCustomerReply();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomerReply
        createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomerReply();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomerReply entity,
                                        LMEnergyExchangeCustomerReply msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        msg.setAcceptDateTime(ConverterHelper.millisecToDate(entity.get_acceptDatetime()));
        msg.setAcceptStatus(entity.get_acceptStatus());
        msg.setCustomerID(entity.get_customerId());
        msg.setEnergyExchangeHourlyCustomer(helper.convertToMessageVector(entity.get_lmEnergyExchangeHourlyCustomers(),
                                                                          LMEnergyExchangeHourlyCustomer.class));
        msg.setEnergyExchangeNotes(entity.get_energyExchangeNotes());
        msg.setIpAddressOfAcceptUser(entity.get_ipAddressOfAcceptUser());
        msg.setNameOfAcceptPerson(entity.get_nameOfAcceptPerson());
        msg.setOfferID(entity.get_offerId());
        msg.setRevisionNumber(entity.get_revisionNumber());
        msg.setUserIDName(entity.get_userIdName());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                        LMEnergyExchangeCustomerReply msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomerReply entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        entity.set_acceptDatetime(ConverterHelper.dateToMillisec(msg.getAcceptDateTime()));
        entity.set_acceptStatus(msg.getAcceptStatus());
        entity.set_customerId(msg.getCustomerID());
        entity.set_energyExchangeNotes(msg.getEnergyExchangeNotes());
        entity.set_ipAddressOfAcceptUser(msg.getIpAddressOfAcceptUser());
        entity
            .set_lmEnergyExchangeHourlyCustomers(helper.convertToEntityList(msg.getEnergyExchangeHourlyCustomer(),
                                                                            com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyCustomer.class));
        entity.set_nameOfAcceptPerson(msg.getNameOfAcceptPerson());
        entity.set_offerId(msg.getOfferID());
        entity.set_revisionNumber(msg.getRevisionNumber());
        entity.set_userIdName(msg.getUserIDName());
    }

}
