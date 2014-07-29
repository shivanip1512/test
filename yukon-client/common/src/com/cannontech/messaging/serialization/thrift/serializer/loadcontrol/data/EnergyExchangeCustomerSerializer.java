package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMCICustomerBase;
import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class EnergyExchangeCustomerSerializer
    extends
    ThriftInheritanceSerializer<LMEnergyExchangeCustomer, LMCICustomerBase, com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomer, com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase> {

    public EnergyExchangeCustomerSerializer(String messageType, CiCustomerBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMEnergyExchangeCustomer> getTargetMessageClass() {
        return LMEnergyExchangeCustomer.class;
    }

    @Override
    protected
        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomer
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomer entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomer();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomer entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMEnergyExchangeCustomer createMessageInstance() {
        return new LMEnergyExchangeCustomer();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomer entity,
                                        LMEnergyExchangeCustomer msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();
        msg.setEnergyExchangeCustomerReplies(helper.convertToMessageVector(entity.get_lmEnergyExchangeCustomerReplies(),
                                                                           LMEnergyExchangeCustomerReply.class));

    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                        LMEnergyExchangeCustomer msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomer entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        entity
            .set_lmEnergyExchangeCustomerReplies(helper.convertToEntityList(msg.getEnergyExchangeCustomerReplies(),
                                                                            com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomerReply.class));
    }
}
