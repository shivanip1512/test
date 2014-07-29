package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMCICustomerBase;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;

public class CiCustomerBaseSerializer extends
    ThriftSerializer<LMCICustomerBase, com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase> {

    protected CiCustomerBaseSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMCICustomerBase> getTargetMessageClass() {
        return LMCICustomerBase.class;
    }

    @Override
    protected LMCICustomerBase createMessageInstance() {
        return new LMCICustomerBase();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase entity,
                                        LMCICustomerBase msg) {
        msg.setCompanyName(entity.get_companyName());
        msg.setCurtailAmount(entity.get_curtailAmount());
        msg.setCurtailmentAgreement(entity.get_curtailmentAgreement());
        msg.setCustomerDemandLevel(entity.get_customerDemandLevel());
        msg.setCustomerID((long)entity.get_customerId());
        msg.setCustomerOrder((long)entity.get_customerOrder());
        msg.setTimeZone(entity.get_timeZone());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMCICustomerBase msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase entity) {
        entity.set_companyName(msg.getCompanyName());
        entity.set_curtailAmount(msg.getCurtailAmount());
        entity.set_curtailmentAgreement(msg.getCurtailmentAgreement());
        entity.set_customerDemandLevel(msg.getCustomerDemandLevel());
        entity.set_customerId(msg.getCustomerID().intValue());
        entity.set_customerOrder(msg.getCustomerOrder().intValue());
        entity.set_timeZone(msg.getTimeZone());
    }

}
