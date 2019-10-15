package com.cannontech.messaging.serialization.thrift.serializer.porter;

import java.util.stream.Collectors;

import com.cannontech.message.porter.message.DynamicPaoInfoDurationKeyEnum;
import com.cannontech.message.porter.message.DynamicPaoInfoPercentageKeyEnum;
import com.cannontech.message.porter.message.DynamicPaoInfoRequest;
import com.cannontech.message.porter.message.DynamicPaoInfoTimestampKeyEnum;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoDurationKeys;
import com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoPercentageKeys;
import com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoTimestampKeys;
import com.google.common.collect.ImmutableMap;

public class DynamicPaoInfoRequestSerializer extends SimpleThriftSerializer implements ThriftByteSerializer<DynamicPaoInfoRequest> {

    private static final ImmutableMap<DynamicPaoInfoDurationKeyEnum, DynamicPaoInfoDurationKeys> durationKeyMapping = ImmutableMap.of(
        DynamicPaoInfoDurationKeyEnum.RFN_VOLTAGE_PROFILE_INTERVAL, DynamicPaoInfoDurationKeys.RFN_VOLTAGE_PROFILE_INTERVAL,
        DynamicPaoInfoDurationKeyEnum.MCT_IED_LOAD_PROFILE_INTERVAL, DynamicPaoInfoDurationKeys.MCT_IED_LOAD_PROFILE_INTERVAL);
    
    private static final ImmutableMap<DynamicPaoInfoTimestampKeyEnum, DynamicPaoInfoTimestampKeys> timestampKeyMapping = ImmutableMap.of(
        DynamicPaoInfoTimestampKeyEnum.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL, DynamicPaoInfoTimestampKeys.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL);

    private static final ImmutableMap<DynamicPaoInfoPercentageKeyEnum, DynamicPaoInfoPercentageKeys> percentageKeyMapping = ImmutableMap.of(
        DynamicPaoInfoPercentageKeyEnum.METER_PROGRAMMING_PROGRESS, DynamicPaoInfoPercentageKeys.METER_PROGRAMMING_PROGRESS);

    @Override
    public byte[] toBytes(DynamicPaoInfoRequest msg) {
        com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoRequest entity = 
                new com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoRequest();

        entity.set_deviceId(msg.getDeviceID());
        entity.set_durationKeys(msg.getDurationKeys().stream()
            .filter(durationKeyMapping::containsKey)
            .map(durationKeyMapping::get)
            .collect(Collectors.toSet()));
        entity.set_timestampKeys(msg.getTimestampKeys().stream()
            .filter(timestampKeyMapping::containsKey)
            .map(timestampKeyMapping::get)
            .collect(Collectors.toSet()));
        entity.set_percentageKeys(msg.getPercentageKeys().stream()
            .filter(percentageKeyMapping::containsKey)
            .map(percentageKeyMapping::get)
            .collect(Collectors.toSet()));
                             
        return serialize(entity);
    }
}
