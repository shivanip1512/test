package com.cannontech.messaging.serialization.thrift.serializer.porter;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.message.porter.message.DynamicPaoInfoKeyEnum;
import com.cannontech.message.porter.message.DynamicPaoInfoRequest;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.DynamicPaoInfoKeys;
import com.cannontech.messaging.serialization.thrift.generated.PorterDynamicPaoInfoRequest;
import com.google.common.collect.ImmutableMap;

public class DynamicPaoInfoRequestSerializer extends ThriftSerializer<DynamicPaoInfoRequest, PorterDynamicPaoInfoRequest> {

    private static final ImmutableMap<DynamicPaoInfoKeyEnum, DynamicPaoInfoKeys> keyMapping = ImmutableMap.of(
            DynamicPaoInfoKeyEnum.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL, DynamicPaoInfoKeys.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL,
            DynamicPaoInfoKeyEnum.RFN_VOLTAGE_PROFILE_INTERVAL, DynamicPaoInfoKeys.RFN_VOLTAGE_PROFILE_INTERVAL);
    
    public DynamicPaoInfoRequestSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<DynamicPaoInfoRequest> getTargetMessageClass() {
        return DynamicPaoInfoRequest.class;
    }

    @Override
    protected DynamicPaoInfoRequest createMessageInstance() {
        return new DynamicPaoInfoRequest();
    }

    @Override
    protected PorterDynamicPaoInfoRequest createThriftEntityInstance() {
        return new PorterDynamicPaoInfoRequest();
    }
    
    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.PorterDynamicPaoInfoRequest entity,
                                        DynamicPaoInfoRequest msg) {
        throw new MethodNotImplementedException();
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DynamicPaoInfoRequest msg,
                                        com.cannontech.messaging.serialization.thrift.generated.PorterDynamicPaoInfoRequest entity) {
        entity.set_deviceId(msg.getDeviceID());
        entity.set_keys(msg.getKeys().stream()
            .filter(new Predicate<DynamicPaoInfoKeyEnum>() {
                @Override
                public boolean test(DynamicPaoInfoKeyEnum key) {
                    return keyMapping.containsKey(key);
                }
            })
            .map(new Function<DynamicPaoInfoKeyEnum, DynamicPaoInfoKeys>() {
                @Override
                public DynamicPaoInfoKeys apply(DynamicPaoInfoKeyEnum key) {
                    return keyMapping.get(key);
                }
            })
            .collect(Collectors.toSet()));
    }

}
