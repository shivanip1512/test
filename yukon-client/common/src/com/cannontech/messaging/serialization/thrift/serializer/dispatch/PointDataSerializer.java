package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import com.cannontech.common.point.PointQuality;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.PointData;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class PointDataSerializer extends ThriftInheritanceSerializer<PointDataMessage, BaseMessage, PointData, Message> {

    public PointDataSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<PointDataMessage> getTargetMessageClass() {
        return PointDataMessage.class;
    }

    @Override
    protected PointData createThrifEntityInstance(Message entityParent) {
        PointData entity = new PointData();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(PointData entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected PointDataMessage createMessageInstance() {
        return new PointDataMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, PointData entity, PointDataMessage msg) {
        msg.setForced(ConverterHelper.IntToUnsigned(entity.get_exemptionStatus()));
        msg.setId(entity.get_id());
        msg.setLimit(ConverterHelper.IntToUnsigned(entity.get_limit()));
        msg.setMillis(entity.get_millis()); // TODO int to long conversion. is it expected?
        msg.setPointQuality(PointQuality.getPointQuality(entity.get_quality()));
        msg.setStr(entity.get_str());
        msg.setTags(ConverterHelper.IntToUnsigned(entity.get_tags()));
        msg.setTime(ConverterHelper.millisecToDate(entity.get_time()));
        msg.setType(entity.get_type());
        msg.setValue(entity.get_value());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, PointDataMessage msg, PointData entity) {
        entity.set_attrib(0);// attributes used to hold this slot
        entity.set_exemptionStatus(ConverterHelper.UnsignedToInt(msg.getForced()));
        entity.set_id(msg.getId());
        entity.set_limit(ConverterHelper.UnsignedToInt(msg.getLimit()));
        entity.set_millis(ConverterHelper.UnsignedToInt(msg.getMillis()));
        entity.set_quality(msg.getPointQuality().getQuality());
        entity.set_str(msg.getStr());
        entity.set_tags(ConverterHelper.UnsignedToInt(msg.getTags()));
        entity.set_time(ConverterHelper.dateToMillisec(msg.getPointDataTimeStamp()));
        entity.set_type(msg.getType());
        entity.set_value(msg.getValue());
    }

}
