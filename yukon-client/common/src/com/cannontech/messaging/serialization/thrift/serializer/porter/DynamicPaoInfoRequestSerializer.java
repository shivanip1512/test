package com.cannontech.messaging.serialization.thrift.serializer.porter;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.cannontech.message.porter.message.DynamicPaoInfoDurationKeyEnum;
import com.cannontech.message.porter.message.DynamicPaoInfoRequest;
import com.cannontech.message.porter.message.DynamicPaoInfoTimestampKeyEnum;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoDurationKeys;
import com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoTimestampKeys;
import com.google.common.collect.ImmutableMap;

public class DynamicPaoInfoRequestSerializer extends SimpleThriftSerializer implements ThriftByteSerializer<DynamicPaoInfoRequest> {

    private static final ImmutableMap<DynamicPaoInfoDurationKeyEnum, DynamicPaoInfoDurationKeys> durationKeyMapping = ImmutableMap.of(
        DynamicPaoInfoDurationKeyEnum.RFN_VOLTAGE_PROFILE_INTERVAL, DynamicPaoInfoDurationKeys.RFN_VOLTAGE_PROFILE_INTERVAL);
    
    private static final ImmutableMap<DynamicPaoInfoTimestampKeyEnum, DynamicPaoInfoTimestampKeys> timestampKeyMapping = ImmutableMap.of(
        DynamicPaoInfoTimestampKeyEnum.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL, DynamicPaoInfoTimestampKeys.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL);

    @Override
    public byte[] toBytes(DynamicPaoInfoRequest msg) {
        com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoRequest entity = 
                new com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoRequest();

        entity.set_deviceId(msg.getDeviceID());
        entity.set_durationKeys(msg.getDurationKeys().stream()
            .filter(new Predicate<DynamicPaoInfoDurationKeyEnum>() {
                @Override
                public boolean test(DynamicPaoInfoDurationKeyEnum key) {
                    return durationKeyMapping.containsKey(key);
                }
            })
            .map(new Function<DynamicPaoInfoDurationKeyEnum, DynamicPaoInfoDurationKeys>() {
                @Override
                public DynamicPaoInfoDurationKeys apply(DynamicPaoInfoDurationKeyEnum key) {
                    return durationKeyMapping.get(key);
                }
            })
            .collect(Collectors.toSet()));
        entity.set_timestampKeys(msg.getTimestampKeys().stream()
            .filter(new Predicate<DynamicPaoInfoTimestampKeyEnum>() {
                @Override
                public boolean test(DynamicPaoInfoTimestampKeyEnum key) {
                    return timestampKeyMapping.containsKey(key);
                }
            })
            .map(new Function<DynamicPaoInfoTimestampKeyEnum, DynamicPaoInfoTimestampKeys>() {
                @Override
                public DynamicPaoInfoTimestampKeys apply(DynamicPaoInfoTimestampKeyEnum key) {
                    return timestampKeyMapping.get(key);
                }
            })
            .collect(Collectors.toSet()));
        
        return serialize(entity);
    }
}
