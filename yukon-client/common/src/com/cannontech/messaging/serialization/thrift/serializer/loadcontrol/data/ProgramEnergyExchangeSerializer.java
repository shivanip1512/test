package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeOffer;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramEnergyExchange;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class ProgramEnergyExchangeSerializer
    extends
    ThriftInheritanceSerializer<LMProgramEnergyExchange, LMProgramBase, com.cannontech.messaging.serialization.thrift.generated.LMProgramEnergyExchange, com.cannontech.messaging.serialization.thrift.generated.LMProgramBase> {

    public ProgramEnergyExchangeSerializer(String messageType, ProgramSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMProgramEnergyExchange> getTargetMessageClass() {
        return LMProgramEnergyExchange.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMProgramEnergyExchange
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMProgramBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMProgramEnergyExchange entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMProgramEnergyExchange();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMProgramBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMProgramEnergyExchange entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMProgramEnergyExchange createMessageInstance() {
        return new LMProgramEnergyExchange();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMProgramEnergyExchange entity,
                                        LMProgramEnergyExchange msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        msg.setCanceledMsg(entity.get_canceledMsg());
        msg.setEnergyExchangeCustomers(helper.convertToMessageVector(entity.get_lmEnergyExchangeCustomers(),
                                                                     LMEnergyExchangeCustomer.class));
        msg.setEnergyExchangeOffers(helper.convertToMessageVector(entity.get_lmEnergyExchangeOffers(),
                                                                  LMEnergyExchangeOffer.class));
        msg.setHeading(entity.get_heading());
        msg.setMessageFooter(entity.get_messageFooter());
        msg.setMessageHeader(entity.get_messageHeader());
        msg.setMinNotifyTime(entity.get_minNotifyTime());
        msg.setStoppedEarlyMsg(entity.get_stoppedEarlyMsg());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                        LMProgramEnergyExchange msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMProgramEnergyExchange entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        entity.set_canceledMsg(msg.getCanceledMsg());
        entity.set_heading(msg.getHeading());
        entity
            .set_lmEnergyExchangeCustomers(helper.convertToEntityList(msg.getEnergyExchangeCustomers(),
                                                                      com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomer.class));
        entity
            .set_lmEnergyExchangeOffers(helper.convertToEntityList(msg.getEnergyExchangeOffers(),
                                                                   com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOffer.class));
        entity.set_messageFooter(msg.getMessageFooter());
        entity.set_messageHeader(msg.getMessageHeader());
        entity.set_minNotifyTime(msg.getMinNotifyTime());
        entity.set_stoppedEarlyMsg(msg.getStoppedEarlyMsg());
    }

}
