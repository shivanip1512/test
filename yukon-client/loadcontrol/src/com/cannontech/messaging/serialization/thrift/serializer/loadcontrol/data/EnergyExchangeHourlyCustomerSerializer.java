package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeHourlyCustomer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyCustomer;

public class EnergyExchangeHourlyCustomerSerializer extends
    ThriftSerializer<EnergyExchangeHourlyCustomer, LMEnergyExchangeHourlyCustomer> {

    protected EnergyExchangeHourlyCustomerSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<EnergyExchangeHourlyCustomer> getTargetMessageClass() {
        return EnergyExchangeHourlyCustomer.class;
    }

    @Override
    protected EnergyExchangeHourlyCustomer createMessageInstance() {
        return new EnergyExchangeHourlyCustomer();
    }

    @Override
    protected LMEnergyExchangeHourlyCustomer createThrifEntityInstance() {
        return new LMEnergyExchangeHourlyCustomer();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMEnergyExchangeHourlyCustomer entity,
                                                   EnergyExchangeHourlyCustomer msg) {
        msg.setAmountCommitted(entity.get_amountCommitted());
        msg.setCustomerID(entity.get_customerId());
        msg.setHour(entity.get_hour());
        msg.setOfferID(entity.get_offerId());
        msg.setRevisionNumber(entity.get_revisionNumber());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, EnergyExchangeHourlyCustomer msg,
                                                   LMEnergyExchangeHourlyCustomer entity) {
        entity.set_amountCommitted(msg.getAmountCommitted());
        entity.set_customerId(msg.getCustomerID());
        entity.set_hour(msg.getHour());
        entity.set_offerId(msg.getOfferID());
        entity.set_revisionNumber(msg.getRevisionNumber());
    }

}
