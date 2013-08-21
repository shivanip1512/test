package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.BankMove;
import com.cannontech.message.capcontrol.model.ItemCommand;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCCapBankMove;
import com.cannontech.messaging.serialization.thrift.generated.CCItemCommand;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class BankMoveSerializer extends
    ThriftInheritanceSerializer<BankMove, ItemCommand, CCCapBankMove, CCItemCommand> {

    public BankMoveSerializer(String messageType, ItemCommandSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<BankMove> getTargetMessageClass() {
        return BankMove.class;
    }

    @Override
    protected CCCapBankMove createThriftEntityInstance(CCItemCommand entityParent) {
        CCCapBankMove entity = new CCCapBankMove();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCItemCommand getThriftEntityParent(CCCapBankMove entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected BankMove createMessageInstance() {
        return new BankMove();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCCapBankMove entity, BankMove msg) {
        msg.setPermanentMove(ConverterHelper.intToBool(entity.get_permanentFlag()));
        msg.setOldFeederId(entity.get_oldFeederId());
        msg.setNewFeederId(entity.get_newFeederId());
        msg.setDisplayOrder((float) entity.get_capSwitchingOrder());
        msg.setCloseOrder((float) entity.get_closeOrder());
        msg.setTripOrder((float) entity.get_tripOrder());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, BankMove msg, CCCapBankMove entity) {
        entity.set_permanentFlag(ConverterHelper.boolToInt(msg.isPermanentMove()));
        entity.set_oldFeederId(msg.getOldFeederId());
        entity.set_newFeederId(msg.getNewFeederId());
        entity.set_capSwitchingOrder(msg.getDisplayOrder());
        entity.set_closeOrder(msg.getCloseOrder());
        entity.set_tripOrder(msg.getTripOrder());
    }

}
