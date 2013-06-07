package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeCustomer;
import com.cannontech.messaging.message.loadcontrol.data.EnergyExchangeOffer;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.messaging.message.loadcontrol.data.ProgramEnergyExchange;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeCustomer;
import com.cannontech.messaging.serialization.thrift.generated.LMEnergyExchangeOffer;
import com.cannontech.messaging.serialization.thrift.generated.LMProgramBase;
import com.cannontech.messaging.serialization.thrift.generated.LMProgramEnergyExchange;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class ProgramEnergyExchangeSerializer extends
    ThriftInheritanceSerializer<ProgramEnergyExchange, Program, LMProgramEnergyExchange, LMProgramBase> {

    public ProgramEnergyExchangeSerializer(String messageType, ProgramSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ProgramEnergyExchange> getTargetMessageClass() {
        return ProgramEnergyExchange.class;
    }

    @Override
    protected LMProgramEnergyExchange createThrifEntityInstance(LMProgramBase entityParent) {
        LMProgramEnergyExchange entity = new LMProgramEnergyExchange();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMProgramBase getThriftEntityParent(LMProgramEnergyExchange entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ProgramEnergyExchange createMessageInstance() {
        return new ProgramEnergyExchange();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMProgramEnergyExchange entity,
                                                   ProgramEnergyExchange msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        msg.setCanceledMsg(entity.get_canceledMsg());
        msg.setEnergyExchangeCustomers(helper.convertToMessageVector(entity.get_lmEnergyExchangeCustomers(),
                                                                     EnergyExchangeCustomer.class));
        msg.setEnergyExchangeOffers(helper.convertToMessageVector(entity.get_lmEnergyExchangeCustomers(),
                                                                  EnergyExchangeOffer.class));
        msg.setHeading(entity.get_heading());
        msg.setMessageFooter(entity.get_messageFooter());
        msg.setMessageHeader(entity.get_messageHeader());
        msg.setMinNotifyTime(entity.get_minNotifyTime());
        msg.setStoppedEarlyMsg(entity.get_stoppedEarlyMsg());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, ProgramEnergyExchange msg,
                                                   LMProgramEnergyExchange entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();

        entity.set_canceledMsg(msg.getCanceledMsg());
        entity.set_heading(msg.getHeading());
        entity.set_lmEnergyExchangeCustomers(helper.convertToEntityList(msg.getEnergyExchangeCustomers(),
                                                                        LMEnergyExchangeCustomer.class));
        entity.set_lmEnergyExchangeOffers(helper.convertToEntityList(msg.getEnergyExchangeCustomers(),
                                                                     LMEnergyExchangeOffer.class));
        entity.set_messageFooter(msg.getMessageFooter());
        entity.set_messageHeader(msg.getMessageHeader());
        entity.set_minNotifyTime(msg.getMinNotifyTime());
        entity.set_stoppedEarlyMsg(msg.getStoppedEarlyMsg());
    }

}
