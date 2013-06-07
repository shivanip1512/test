package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.CiCustomerBase;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMCICustomerBase;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class CiCustomerBaseSerializer extends ThriftSerializer<CiCustomerBase, LMCICustomerBase> {

    protected CiCustomerBaseSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<CiCustomerBase> getTargetMessageClass() {
        return CiCustomerBase.class;
    }

    @Override
    protected CiCustomerBase createMessageInstance() {
        return new CiCustomerBase();
    }

    @Override
    protected LMCICustomerBase createThrifEntityInstance() {
        return new LMCICustomerBase();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMCICustomerBase entity, CiCustomerBase msg) {
        msg.setCompanyName(entity.get_companyName());
        msg.setCurtailAmount(entity.get_curtailAmount());
        msg.setCurtailmentAgreement(entity.get_curtailmentAgreement());
        msg.setCustomerDemandLevel(entity.get_customerDemandLevel());
        msg.setCustomerId(ConverterHelper.IntToUnsigned(entity.get_customerId()));
        msg.setCustomerOrder(ConverterHelper.IntToUnsigned(entity.get_customerOrder()));
        msg.setTimeZone(entity.get_timeZone());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CiCustomerBase msg, LMCICustomerBase entity) {
        entity.set_companyName(msg.getCompanyName());
        entity.set_curtailAmount(msg.getCurtailAmount());
        entity.set_curtailmentAgreement(msg.getCurtailmentAgreement());
        entity.set_customerDemandLevel(msg.getCustomerDemandLevel());
        entity.set_customerId(ConverterHelper.UnsignedToInt(msg.getCustomerId()));
        entity.set_customerOrder(ConverterHelper.UnsignedToInt(msg.getCustomerOrder()));
        entity.set_timeZone(msg.getTimeZone());
    }

}
