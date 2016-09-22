package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import com.cannontech.common.point.PointQuality;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class PointDataSerializer
    extends
    ThriftInheritanceSerializer<PointData, Message, com.cannontech.messaging.serialization.thrift.generated.PointData, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public PointDataSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<PointData> getTargetMessageClass() {
        return PointData.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.PointData
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.PointData entity =
            new com.cannontech.messaging.serialization.thrift.generated.PointData();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.PointData entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected PointData createMessageInstance() {
        return new PointData();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.PointData entity,
                                        PointData msg) {
        msg.setId(entity.get_id());
        msg.setMillis(ConverterHelper.IntToUnsigned(entity.get_millis()));
        msg.setPointQuality(PointQuality.getPointQuality(entity.get_quality()));
        msg.setStr(entity.get_str());
        msg.setTags(ConverterHelper.IntToUnsigned(entity.get_tags()));
        msg.setTime(ConverterHelper.millisecToDate(entity.get_time()));
        msg.setType(entity.get_type());
        msg.setValue(entity.get_value());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, PointData msg,
                                        com.cannontech.messaging.serialization.thrift.generated.PointData entity) {
        entity.set_id(msg.getId());
        entity.set_millis((short)ConverterHelper.UnsignedToInt(msg.getMillis()));
        entity.set_quality((byte)msg.getPointQuality().getQuality());
        entity.set_str(msg.getStr());
        entity.set_tags(ConverterHelper.UnsignedToInt(msg.getTags()));
        entity.set_time(ConverterHelper.dateToMillisec(msg.getPointDataTimeStamp()));
        entity.set_type((byte)msg.getType());
        entity.set_value(msg.getValue());
    }

}
