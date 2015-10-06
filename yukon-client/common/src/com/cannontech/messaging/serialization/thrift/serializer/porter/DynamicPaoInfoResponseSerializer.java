package com.cannontech.messaging.serialization.thrift.serializer.porter;

import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.message.porter.message.DynamicPaoInfoDurationKeyEnum;
import com.cannontech.message.porter.message.DynamicPaoInfoResponse;
import com.cannontech.message.porter.message.DynamicPaoInfoTimestampKeyEnum;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoDurationKeys;
import com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoTimestampKeys;
import com.google.common.collect.ImmutableMap;

public class DynamicPaoInfoResponseSerializer extends SimpleThriftSerializer implements ThriftByteDeserializer<DynamicPaoInfoResponse> {

    private static final ImmutableMap<DynamicPaoInfoDurationKeys, DynamicPaoInfoDurationKeyEnum> durationKeyMapping = ImmutableMap.of(
        DynamicPaoInfoDurationKeys.RFN_VOLTAGE_PROFILE_INTERVAL, DynamicPaoInfoDurationKeyEnum.RFN_VOLTAGE_PROFILE_INTERVAL);
    
    private static final ImmutableMap<DynamicPaoInfoTimestampKeys, DynamicPaoInfoTimestampKeyEnum> timestampKeyMapping = ImmutableMap.of(
        DynamicPaoInfoTimestampKeys.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL, DynamicPaoInfoTimestampKeyEnum.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL);
    
    @Override
    public DynamicPaoInfoResponse fromBytes(byte[] msgBytes) {

        com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoResponse entity = new 
                com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoResponse();
        
        deserialize(msgBytes, entity);
        
        //TODO JAVA 8

        DynamicPaoInfoResponse msg = new DynamicPaoInfoResponse();
        
        msg.setDeviceID(entity.get_deviceId());

        msg.setDurationValues(entity.get_durationValues().entrySet().stream()
            .filter(new Predicate<Entry<DynamicPaoInfoDurationKeys, Long>>() {
                @Override
                public boolean test(Entry<DynamicPaoInfoDurationKeys, Long> entry) {
                    return durationKeyMapping.containsKey(entry.getKey());
                }
            })
            .collect(Collectors.toMap(
                new Function<Entry<DynamicPaoInfoDurationKeys, Long>, DynamicPaoInfoDurationKeyEnum>() {
                    @Override
                    public DynamicPaoInfoDurationKeyEnum apply(Entry<DynamicPaoInfoDurationKeys, Long> entry) {
                        return durationKeyMapping.get(entry.getKey());
                    }
                }, 
                new Function<Entry<DynamicPaoInfoDurationKeys, Long>, Duration>() {
                    @Override
                    public Duration apply(Entry<DynamicPaoInfoDurationKeys, Long> entry) {
                        return new Duration(entry.getValue());
                    }
                })));

        msg.setTimestampValues(entity.get_timestampValues().entrySet().stream()
            .filter(new Predicate<Entry<DynamicPaoInfoTimestampKeys, Long>>() {
                @Override
                public boolean test(Entry<DynamicPaoInfoTimestampKeys, Long> entry) {
                    return timestampKeyMapping.containsKey(entry.getKey());
                }
            })
            .collect(Collectors.toMap(
                new Function<Entry<DynamicPaoInfoTimestampKeys, Long>, DynamicPaoInfoTimestampKeyEnum>() {
                    @Override
                    public DynamicPaoInfoTimestampKeyEnum apply(Entry<DynamicPaoInfoTimestampKeys, Long> entry) {
                        return timestampKeyMapping.get(entry.getKey());
                    }
                }, 
                new Function<Entry<DynamicPaoInfoTimestampKeys, Long>, Instant>() {
                    @Override
                    public Instant apply(Entry<DynamicPaoInfoTimestampKeys, Long> entry) {
                        return new Instant(entry.getValue());
                    }
                })));
        
        return msg;
    }

}
