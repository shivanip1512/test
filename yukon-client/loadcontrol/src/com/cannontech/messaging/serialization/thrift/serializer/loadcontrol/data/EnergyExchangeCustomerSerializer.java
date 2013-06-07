package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.CiCustomerBase;
import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeCustomer;
import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeCustomerReply;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomer;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomerReply;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class EnergyExchangeCustomerSerializer extends
    ThriftInheritanceSerializer<EnergyExchangeCustomer, CiCustomerBase, LMEnergyExchangeCustomer, LMCICustomerBase> {

    public EnergyExchangeCustomerSerializer(String messageType, CiCustomerBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<EnergyExchangeCustomer> getTargetMessageClass() {
        return EnergyExchangeCustomer.class;
    }

    @Override
    protected LMEnergyExchangeCustomer createThrifEntityInstance(LMCICustomerBase entityParent) {
        LMEnergyExchangeCustomer entity = new LMEnergyExchangeCustomer();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMCICustomerBase getThriftEntityParent(LMEnergyExchangeCustomer entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected EnergyExchangeCustomer createMessageInstance() {
        return new EnergyExchangeCustomer();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMEnergyExchangeCustomer entity,
                                                   EnergyExchangeCustomer msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();
        msg.setEnergyExchangeCustomerReplies(helper.convertToMessageVector(entity.get_lmEnergyExchangeCustomerReplies(),
                                                                           EnergyExchangeCustomerReply.class));

    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, EnergyExchangeCustomer msg,
                                                   LMEnergyExchangeCustomer entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        entity.set_lmEnergyExchangeCustomerReplies(helper.convertToEntityList(msg.getEnergyExchangeCustomerReplies(),
                                                                              LMEnergyExchangeCustomerReply.class));
    }
}
