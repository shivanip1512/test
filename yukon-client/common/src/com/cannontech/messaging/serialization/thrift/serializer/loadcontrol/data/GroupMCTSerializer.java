package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupMCT;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupEmetcon;

// This Serializer is different from the other ones because the type hierarchy is not the same between CPP and Java
// in this particular case LMGroupMCT derives from LMDirectGroupBase where in CPP (thrift) it derives from
// LMGroupEmetcon
// This additional level (in C++) is empty so it does not change much but we have extra code in this class to handle
// this particular case
public class GroupMCTSerializer
    extends
    ThriftInheritanceSerializer<LMGroupMCT, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupMCT, LMGroupBase> {

    public GroupMCTSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupMCT> getTargetMessageClass() {
        return LMGroupMCT.class;
    }

    /*
     * Due to the additional level of inheritance of the C++ (thrift) side, we have to manually create the extra Thrift
     * Base type <LMGroupEmetcon>
     */
    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupMCT
        createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupMCT entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMGroupMCT();
        LMGroupEmetcon oneLevelBaseEntity = new LMGroupEmetcon();
        oneLevelBaseEntity.set_baseMessage(entityParent);
        entity.set_baseMessage(oneLevelBaseEntity);
        return entity;
    }

    /*
     * Due to the additional level of inheritance of the C++ (thrift) side, we have to manually skip over the extra
     * Thrift Base type <LMGroupEmetcon>
     */
    @Override
    protected LMGroupBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupMCT entity) {
        return entity.get_baseMessage().get_baseMessage();
    }

    @Override
    protected LMGroupMCT createMessageInstance() {
        return new LMGroupMCT();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupMCT entity,
                                        LMGroupMCT msg) {}

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupMCT msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupMCT entity) {}
}
