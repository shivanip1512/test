package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeCustomerReply;
import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeHourlyCustomer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomerReply;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyCustomer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeCustomerReplySerializer extends
    ThriftSerializer<EnergyExchangeCustomerReply, LMEnergyExchangeCustomerReply> {

    protected EnergyExchangeCustomerReplySerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<EnergyExchangeCustomerReply> getTargetMessageClass() {
        return EnergyExchangeCustomerReply.class;
    }

    @Override
    protected EnergyExchangeCustomerReply createMessageInstance() {
        return new EnergyExchangeCustomerReply();
    }

    @Override
    protected LMEnergyExchangeCustomerReply createThrifEntityInstance() {
        return new LMEnergyExchangeCustomerReply();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMEnergyExchangeCustomerReply entity,
                                                   EnergyExchangeCustomerReply msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        msg.setAcceptDateTime(ConverterHelper.millisecToDate(entity.get_acceptDatetime()));
        msg.setAcceptStatus(entity.get_acceptStatus());
        msg.setCustomerId(entity.get_customerId());
        msg.setEnergyExchangeHourlyCustomer(helper.convertToMessageVector(entity.get_lmEnergyExchangeHourlyCustomers(),
                                                                          EnergyExchangeHourlyCustomer.class));
        msg.setEnergyExchangeNotes(entity.get_energyExchangeNotes());
        msg.setIpAddressOfAcceptUser(entity.get_ipAddressOfAcceptUser());
        msg.setNameOfAcceptPerson(entity.get_nameOfAcceptPerson());
        msg.setOfferId(entity.get_offerId());
        msg.setRevisionNumber(entity.get_revisionNumber());
        msg.setUserIdName(entity.get_userIdName());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, EnergyExchangeCustomerReply msg,
                                                   LMEnergyExchangeCustomerReply entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        entity.set_acceptDatetime(ConverterHelper.dateToMillisec(msg.getAcceptDateTime()));
        entity.set_acceptStatus(msg.getAcceptStatus());
        entity.set_customerId(msg.getCustomerId());
        entity.set_energyExchangeNotes(msg.getEnergyExchangeNotes());
        entity.set_ipAddressOfAcceptUser(msg.getIpAddressOfAcceptUser());
        entity.set_lmEnergyExchangeHourlyCustomers(helper.convertToEntityList(msg.getEnergyExchangeHourlyCustomer(),
                                                                              LMEnergyExchangeHourlyCustomer.class));
        entity.set_nameOfAcceptPerson(msg.getNameOfAcceptPerson());
        entity.set_offerId(msg.getOfferId());
        entity.set_revisionNumber(msg.getRevisionNumber());
        entity.set_userIdName(msg.getUserIdName());
    }

}
