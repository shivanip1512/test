package com.cannontech.messaging.serialization.thrift.serializer.porter;

import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.joda.time.Instant;

import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.message.porter.message.DynamicPaoInfoKeyEnum;
import com.cannontech.message.porter.message.DynamicPaoInfoResponse;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.DynamicPaoInfoKeys;
import com.cannontech.messaging.serialization.thrift.generated.DynamicPaoInfoTypes;
import com.cannontech.messaging.serialization.thrift.generated.PorterDynamicPaoInfoResponse;
import com.google.common.collect.ImmutableMap;

public class DynamicPaoInfoResponseSerializer extends ThriftSerializer<DynamicPaoInfoResponse, PorterDynamicPaoInfoResponse> {

    private static final ImmutableMap<DynamicPaoInfoKeys, DynamicPaoInfoKeyEnum> keyMapping = ImmutableMap.of(
            DynamicPaoInfoKeys.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL, DynamicPaoInfoKeyEnum.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL,
            DynamicPaoInfoKeys.RFN_VOLTAGE_PROFILE_INTERVAL, DynamicPaoInfoKeyEnum.RFN_VOLTAGE_PROFILE_INTERVAL);
    
    public DynamicPaoInfoResponseSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<DynamicPaoInfoResponse> getTargetMessageClass() {
        return DynamicPaoInfoResponse.class;
    }

    @Override
    protected DynamicPaoInfoResponse createMessageInstance() {
        return new DynamicPaoInfoResponse();
    }

    @Override
    protected PorterDynamicPaoInfoResponse createThriftEntityInstance() {
        return new PorterDynamicPaoInfoResponse();
    }
    
    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.PorterDynamicPaoInfoResponse entity,
                                        DynamicPaoInfoResponse msg) {
        msg.setDeviceID(entity.get_deviceId());
        
        Function<Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes>, DynamicPaoInfoKeys> getEntryKey = Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes>::getKey;
        Function<Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes>, DynamicPaoInfoTypes> getEntryValue = Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes>::getValue;

        //TODO JAVA 8
        msg.setTimeValues(entity.get_values().entrySet().stream()
            .filter(getEntryValue.andThen(DynamicPaoInfoTypes::isSet_time)::apply)
            .filter(getEntryKey.andThen(keyMapping::containsKey)::apply)
            .collect(Collectors.toMap(
                    getEntryKey.andThen(keyMapping::get),
                    getEntryValue.andThen(DynamicPaoInfoTypes::get_time).andThen(Instant::new))));
        
        msg.setStringValues(entity.get_values().entrySet().stream()
            .filter(getEntryValue.andThen(DynamicPaoInfoTypes::isSet_string)::apply)
            .filter(getEntryKey.andThen(keyMapping::containsKey)::apply)
            .collect(Collectors.toMap(
                    getEntryKey.andThen(keyMapping::get),
                    getEntryValue.andThen(DynamicPaoInfoTypes::get_string))));
        
        msg.setLongValues(entity.get_values().entrySet().stream()
            .filter(getEntryValue.andThen(DynamicPaoInfoTypes::isSet_integer)::apply)
            .filter(getEntryKey.andThen(keyMapping::containsKey)::apply)
            .collect(Collectors.toMap(
                    getEntryKey.andThen(keyMapping::get),
                    getEntryValue.andThen(DynamicPaoInfoTypes::get_integer))));
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DynamicPaoInfoResponse msg,
                                        com.cannontech.messaging.serialization.thrift.generated.PorterDynamicPaoInfoResponse entity) {
        throw new MethodNotImplementedException();
    }

}
