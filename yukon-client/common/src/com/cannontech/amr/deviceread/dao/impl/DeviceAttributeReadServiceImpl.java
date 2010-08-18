package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;

public class DeviceAttributeReadServiceImpl implements DeviceAttributeReadService {
    
    private static final Logger log = YukonLogManager.getLogger(DeviceAttributeReadServiceImpl.class);
    
    private ImmutableMap<DeviceAttributeReadStrategyType, DeviceAttributeReadStrategy> strategies = ImmutableMap.of();
    
    @Override
    public boolean isReadable(Iterable<? extends YukonPao> devices, Set<Attribute> attributes,
            LiteYukonUser user) {
        
       ImmutableMultimap<PaoType, ? extends YukonPao> paoTypes = PaoUtils.mapPaoTypes(devices);
        
        // loop through each PaoType and strategy
        for (PaoType paoType : paoTypes.keySet()) {
            for (DeviceAttributeReadStrategyType strategy : strategies.keySet()) {
                DeviceAttributeReadStrategy impl = strategies.get(strategy);
                if (impl.canRead(paoType)) {
                    ImmutableCollection<? extends YukonPao> immutableCollection = paoTypes.get(paoType);
                    Iterable<PaoIdentifier> paoIdentifiers = PaoUtils.asPaoIdentifiers(immutableCollection);
                    boolean readable = impl.isReadable(paoIdentifiers, attributes, user);
                    if (readable) {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void initiateRead(Iterable<? extends YukonPao> devices,
            Set<? extends Attribute> attributes, final DeviceAttributeReadCallback delegateCallback, DeviceRequestType type, LiteYukonUser user) {
        
        // this will represent the "plan" for how all of the input devices will be read
        Multimap<DeviceAttributeReadStrategyType, PaoIdentifier> thePlan = HashMultimap.create();
        
        ImmutableMultimap<PaoType, ? extends YukonPao> paoTypes = PaoUtils.mapPaoTypes(devices);
        
        // loop through each PaoType and strategy, add applicable PaoIdentifiers to the plan
        for (PaoType paoType : paoTypes.keySet()) {
            DeviceAttributeReadStrategyType foundStrategy = null;
            for (DeviceAttributeReadStrategyType strategy : strategies.keySet()) {
                DeviceAttributeReadStrategy impl = strategies.get(strategy);
                if (impl.canRead(paoType)) {
                    foundStrategy = strategy;
                    break;
                }
            }
            ImmutableCollection<? extends YukonPao> immutableCollection = paoTypes.get(paoType);
            Iterable<PaoIdentifier> paoIdentifiers = PaoUtils.asPaoIdentifiers(immutableCollection);
            if (foundStrategy == null) {
                for (PaoIdentifier pao : paoIdentifiers) {
                    MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("no strategy", paoType);
                    DeviceAttributeReadError strategyError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.NO_STRATEGY, summary);
                    delegateCallback.receivedError(pao, strategyError );
                }
            } else {
                thePlan.putAll(foundStrategy, paoIdentifiers);
            }
        }
        
        int expectedCallbacks = thePlan.keySet().size();
        if (expectedCallbacks == 0) {
            delegateCallback.complete();
            return;
        }
        final AtomicInteger completionCounter = new AtomicInteger(expectedCallbacks);
        
        DeviceAttributeReadCallback strategyCallback = new DeviceAttributeReadCallback() {
            public void complete() {
                int count = completionCounter.decrementAndGet();
                if (count == 0) {
                    delegateCallback.complete();
                }
            }
            
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                delegateCallback.receivedError(pao, error);
            }
            
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                delegateCallback.receivedValue(pao, value);
            }
            
            @Override
            public void receivedException(DeviceAttributeReadError exception) {
                delegateCallback.receivedException(exception);
            }
            
        };
        
        for (DeviceAttributeReadStrategyType strategy : thePlan.keySet()) {
            DeviceAttributeReadStrategy impl = strategies.get(strategy);
            Collection<PaoIdentifier> devicesForThisStrategy = thePlan.get(strategy);
            
            impl.initiateRead(devicesForThisStrategy, attributes, strategyCallback, type, user);
        }
    }
    
    @Autowired
    public void setStrategies(List<DeviceAttributeReadStrategy> strategyList) {
        Builder<DeviceAttributeReadStrategyType, DeviceAttributeReadStrategy> builder = ImmutableMap.builder();
        for (DeviceAttributeReadStrategy strategy : strategyList) {
            builder.put(strategy.getType(), strategy);
        }
        strategies = builder.build();
    }

}
