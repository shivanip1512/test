package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupMCT;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupEmetcon;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupMCT;


// This Serializer is different from the other ones because the type hierarchy is not the same between CPP and Java
// in this particular case GroupMCT derives from DirectGroupBase where in CPP (thrift) it derives from LMGroupEmetcon
// This additional level (in C++) is empty so it does not change much but we have extra code in this class to handle this particular case 
public class GroupMCTSerializer extends ThriftInheritanceSerializer<GroupMCT, DirectGroupBase, LMGroupMCT, LMGroupBase> {

    public GroupMCTSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupMCT> getTargetMessageClass() {
        return GroupMCT.class;
    }

    /*
     * Due to the additional level of inheritance of the C++ (thrift) side, we have to manually create
     * the extra Thrift Base type <LMGroupEmetcon>
     * */
    @Override
    protected LMGroupMCT createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupMCT entity = new LMGroupMCT();
        LMGroupEmetcon oneLevelBaseEntity = new LMGroupEmetcon();
        oneLevelBaseEntity.set_baseMessage(entityParent);
        entity.set_baseMessage(oneLevelBaseEntity);
        return entity;
    }

    /*
     * Due to the additional level of inheritance of the C++ (thrift) side, we have to manually skip
     * over the extra Thrift Base type <LMGroupEmetcon>
     * */
    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupMCT entity) {
        return entity.get_baseMessage().get_baseMessage();
    }

    @Override
    protected GroupMCT createMessageInstance() {
        return new GroupMCT();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupMCT entity, GroupMCT msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupMCT msg, LMGroupMCT entity) {}
}
