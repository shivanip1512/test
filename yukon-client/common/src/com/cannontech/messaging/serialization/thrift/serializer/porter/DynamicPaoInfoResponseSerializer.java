package com.cannontech.messaging.serialization.thrift.serializer.porter;

import java.util.stream.Collectors;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.message.porter.message.DynamicPaoInfoDurationKeyEnum;
import com.cannontech.message.porter.message.DynamicPaoInfoPercentageKeyEnum;
import com.cannontech.message.porter.message.DynamicPaoInfoResponse;
import com.cannontech.message.porter.message.DynamicPaoInfoTimestampKeyEnum;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoDurationKeys;
import com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoPercentageKeys;
import com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoTimestampKeys;
import com.google.common.collect.ImmutableMap;

public class DynamicPaoInfoResponseSerializer extends SimpleThriftSerializer implements ThriftByteDeserializer<DynamicPaoInfoResponse> {

    private static final ImmutableMap<DynamicPaoInfoDurationKeys, DynamicPaoInfoDurationKeyEnum> durationKeyMapping = ImmutableMap.of(
        DynamicPaoInfoDurationKeys.RFN_VOLTAGE_PROFILE_INTERVAL, DynamicPaoInfoDurationKeyEnum.RFN_VOLTAGE_PROFILE_INTERVAL);
    
    private static final ImmutableMap<DynamicPaoInfoTimestampKeys, DynamicPaoInfoTimestampKeyEnum> timestampKeyMapping = ImmutableMap.of(
        DynamicPaoInfoTimestampKeys.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL, DynamicPaoInfoTimestampKeyEnum.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL);
    
    private static final ImmutableMap<DynamicPaoInfoPercentageKeys, DynamicPaoInfoPercentageKeyEnum> percentageKeyMapping = ImmutableMap.of(
        DynamicPaoInfoPercentageKeys.METER_PROGRAMMING_PROGRESS, DynamicPaoInfoPercentageKeyEnum.METER_PROGRAMMING_PROGRESS);
                                                                                                                                     
    @Override
    public DynamicPaoInfoResponse fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.porter.DynamicPaoInfoResponse();
        
        deserialize(msgBytes, entity);
        
        DynamicPaoInfoResponse msg = new DynamicPaoInfoResponse();
        
        msg.setDeviceID(entity.get_deviceId());

        msg.setDurationValues(entity.get_durationValues().entrySet().stream()
            .filter(entry -> durationKeyMapping.containsKey(entry.getKey()))
            .collect(Collectors.toMap(
                entry -> durationKeyMapping.get(entry.getKey()),
                entry -> new Duration(entry.getValue()))));

        msg.setTimestampValues(entity.get_timestampValues().entrySet().stream()
            .filter(entry -> timestampKeyMapping.containsKey(entry.getKey()))
            .collect(Collectors.toMap(
                entry -> timestampKeyMapping.get(entry.getKey()),
                entry -> new Instant(entry.getValue()))));
        
        msg.setPercentageValues(entity.get_percentageValues().entrySet().stream()
            .filter(entry -> percentageKeyMapping.containsKey(entry.getKey()))
            .collect(Collectors.toMap(
                entry -> percentageKeyMapping.get(entry.getKey()),
                entry -> entry.getValue())));
                           
        return msg;
    }

}
