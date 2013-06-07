package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.messaging.message.loadcontrol.data.ConstraintViolation;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMConstraintViolation;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ConstraintViolationSerializer extends ThriftSerializer<ConstraintViolation, LMConstraintViolation> {

    public ConstraintViolationSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<ConstraintViolation> getTargetMessageClass() {
        return ConstraintViolation.class;
    }

    @Override
    protected ConstraintViolation createMessageInstance() {
        return new ConstraintViolation();
    }

    @Override
    protected LMConstraintViolation createThrifEntityInstance() {
        return new LMConstraintViolation();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMConstraintViolation entity, ConstraintViolation msg) {
        msg.setDoubleParams(entity.get_doubleParams());
        msg.setIntegerParams(entity.get_integerParams());
        msg.setStringParams(entity.get_stringParams());

        List<Date> dateList = new ArrayList<Date>(entity.get_datetimeParams().size());
        for (long millisecDate : entity.get_datetimeParams()) {
            dateList.add(ConverterHelper.millisecToDate(millisecDate));
        }
        msg.setDateTimeParams(dateList);
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ConstraintViolation msg, LMConstraintViolation entity) {
        entity.set_doubleParams(msg.getDoubleParams());
        entity.set_integerParams(msg.getIntegerParams());
        entity.set_stringParams(msg.getStringParams());

        List<Long> longList = new ArrayList<Long>(msg.getDatetimeParams().size());
        for (Date date : msg.getDatetimeParams()) {
            longList.add(ConverterHelper.dateToMillisec(date));
        }
        entity.set_datetimeParams(longList);
    }
}
