package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;

public class EnergyExchangeHourlyCustomerSerializer
    extends
    ThriftSerializer<LMEnergyExchangeHourlyCustomer, com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyCustomer> {

    protected EnergyExchangeHourlyCustomerSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMEnergyExchangeHourlyCustomer> getTargetMessageClass() {
        return LMEnergyExchangeHourlyCustomer.class;
    }

    @Override
    protected LMEnergyExchangeHourlyCustomer createMessageInstance() {
        return new LMEnergyExchangeHourlyCustomer();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyCustomer
        createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyCustomer();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyCustomer entity,
                                        LMEnergyExchangeHourlyCustomer msg) {
        msg.setAmountCommitted(entity.get_amountCommitted());
        msg.setCustomerID(entity.get_customerId());
        msg.setHour(entity.get_hour());
        msg.setOfferID(entity.get_offerId());
        msg.setRevisionNumber(entity.get_revisionNumber());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                        LMEnergyExchangeHourlyCustomer msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeHourlyCustomer entity) {
        entity.set_amountCommitted(msg.getAmountCommitted());
        entity.set_customerId(msg.getCustomerID());
        entity.set_hour(msg.getHour());
        entity.set_offerId(msg.getOfferID());
        entity.set_revisionNumber(msg.getRevisionNumber());
    }

}
