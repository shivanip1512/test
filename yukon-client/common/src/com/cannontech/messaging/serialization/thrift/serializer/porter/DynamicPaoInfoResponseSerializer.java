package com.cannontech.messaging.serialization.thrift.serializer.porter;

import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
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

public class DynamicPaoInfoResponseSerializer
    extends
    ThriftSerializer<DynamicPaoInfoResponse, PorterDynamicPaoInfoResponse> {

    static final ImmutableMap<DynamicPaoInfoKeys, DynamicPaoInfoKeyEnum> keyMapping = ImmutableMap.of(
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
        
        abstract class EntryValuePredicate implements Predicate<Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes>> {
            @Override
            public boolean test(Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes> entry) {
                return test(entry.getValue());
            }
            abstract boolean test(DynamicPaoInfoTypes value);
        }
        
        abstract class MapEntryValueTo<R> implements Function<Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes>, R> {
            @Override
            public R apply(Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes> entry) {
                return map(entry.getValue());
            }
            abstract R map(DynamicPaoInfoTypes value);
        }
        
        Predicate<Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes>> keyMappingContainsEntryKey = new Predicate<Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes>>() {
            @Override
            public boolean test(Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes> entry) {
                return keyMapping.containsKey(entry.getKey());
            }
        };
        
        Function<Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes>, DynamicPaoInfoKeyEnum> getKeyMappingForEntryKey = new Function<Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes>, DynamicPaoInfoKeyEnum>() {
            @Override
            public DynamicPaoInfoKeyEnum apply(Entry<DynamicPaoInfoKeys, DynamicPaoInfoTypes> entry) {
                return keyMapping.get(entry.getKey());
            }
        };

        msg.setTimeValues(entity.get_values().entrySet().stream()
            .filter(new EntryValuePredicate() {
                @Override
                boolean test(DynamicPaoInfoTypes value) {
                    return value.isSet_time();
                }
            })
            .filter(keyMappingContainsEntryKey)
            .collect(Collectors.toMap(
                    getKeyMappingForEntryKey,
                    new MapEntryValueTo<Instant>() {
                        @Override
                        Instant map(DynamicPaoInfoTypes value) {
                            return new Instant(value.get_time());
                        }
                    })));
        
        msg.setStringValues(entity.get_values().entrySet().stream()
            .filter(new EntryValuePredicate() {
                @Override
                boolean test(DynamicPaoInfoTypes value) {
                    return value.isSet_string();
                }
            })
            .filter(keyMappingContainsEntryKey)
            .collect(Collectors.toMap(
                    getKeyMappingForEntryKey,
                    new MapEntryValueTo<String>() {
                        @Override
                        String map(DynamicPaoInfoTypes value) {
                            return value.get_string();
                        }
                    })));
        
        msg.setLongValues(entity.get_values().entrySet().stream()
            .filter(new EntryValuePredicate() {
                @Override
                boolean test(DynamicPaoInfoTypes value) {
                    return value.isSet_integer();
                }
            })
            .filter(keyMappingContainsEntryKey)
            .collect(Collectors.toMap(
                    getKeyMappingForEntryKey,
                    new MapEntryValueTo<Long>() {
                        @Override
                        Long map(DynamicPaoInfoTypes value) {
                            return value.get_integer();
                        }
                    })));
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DynamicPaoInfoResponse msg,
                                        com.cannontech.messaging.serialization.thrift.generated.PorterDynamicPaoInfoResponse entity) {
        throw new MethodNotImplementedException();
    }

}
