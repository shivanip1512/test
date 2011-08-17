package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class DeviceAttributeReadServiceImpl implements DeviceAttributeReadService {
    
    private static final Logger log = YukonLogManager.getLogger(DeviceAttributeReadServiceImpl.class);
    
    private AttributeService attributeService;
    
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
                    List<PaoMultiPointIdentifier> devicesAndPoints = getPaoMultiPointIdentifiers(immutableCollection, attributes);
                    boolean readable = impl.isReadable(devicesAndPoints, user);
                    if (readable) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void initiateRead(Iterable<? extends YukonPao> devices,
                             Set<? extends Attribute> attributes, 
                             final DeviceAttributeReadCallback delegateCallback, 
                             DeviceRequestType type, 
                             LiteYukonUser user) {
        
        // this will represent the "plan" for how all of the input devices will be read
        Map<DeviceAttributeReadStrategyType, Collection<PaoMultiPointIdentifier>> thePlan = 
            Maps.newEnumMap(DeviceAttributeReadStrategyType.class);
        
        // we need to resolve the attributes first to figure out which physical devices we'll be reading
        final List<PaoMultiPointIdentifier> devicesAndPoints = getPaoMultiPointIdentifiers(devices, attributes);

        Multimap<PaoType, PaoMultiPointIdentifier> byPhysicalPaoType = ArrayListMultimap.create(1, devicesAndPoints.size());
        for (PaoMultiPointIdentifier multiPoints : devicesAndPoints) {
            byPhysicalPaoType.put(multiPoints.getPao().getPaoType(), multiPoints);
        }
        
        // loop through each PaoType and strategy, add applicable PaoIdentifiers to the plan
        for (PaoType paoType : byPhysicalPaoType.keySet()) {
            DeviceAttributeReadStrategyType foundStrategy = null;
            for (DeviceAttributeReadStrategyType strategy : strategies.keySet()) {
                DeviceAttributeReadStrategy impl = strategies.get(strategy);
                if (impl.canRead(paoType)) {
                    foundStrategy = strategy;
                    break;
                }
            }
            Collection<PaoMultiPointIdentifier> paoPointIdentifiersForType = byPhysicalPaoType.get(paoType);
            if (foundStrategy == null) {
                for (PaoMultiPointIdentifier paoMultiPoints : paoPointIdentifiersForType) {
                    MessageSourceResolvable summary = 
                        YukonMessageSourceResolvable.createDefaultWithoutCode("no strategy for " + paoType);
                    DeviceAttributeReadError strategyError = 
                        new DeviceAttributeReadError(DeviceAttributeReadErrorType.NO_STRATEGY, summary);
                    delegateCallback.receivedError(paoMultiPoints.getPao(), strategyError);
                }
            } else {
                thePlan.put(foundStrategy, paoPointIdentifiersForType);
            }
        }
        
        int expectedCallbacks = thePlan.keySet().size();
        if (expectedCallbacks == 0) {
            delegateCallback.complete();
            return;
        }
        final AtomicInteger completionCounter = new AtomicInteger(expectedCallbacks);
        
        DeviceAttributeReadStrategyCallback strategyCallback = new DeviceAttributeReadStrategyCallback() {
            public void complete() {
                int count = completionCounter.decrementAndGet();
                if (count == 0) {
                    delegateCallback.complete();
                }
            }
            
            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                delegateCallback.receivedError(pao, error);
            }
            
            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                delegateCallback.receivedValue(pao, value);
            }
            
            @Override
            public void receivedLastValue(PaoIdentifier pao) {
                delegateCallback.receivedLastValue(pao);
            }
            
            @Override
            public void receivedException(DeviceAttributeReadError exception) {
                delegateCallback.receivedException(exception);
            }
            
        };
        
        for (DeviceAttributeReadStrategyType strategy : thePlan.keySet()) {
            DeviceAttributeReadStrategy impl = strategies.get(strategy);
            Collection<PaoMultiPointIdentifier> devicesForThisStrategy = thePlan.get(strategy);
            
            impl.initiateRead(devicesForThisStrategy, strategyCallback, type, user);
        }
    }

    private List<PaoMultiPointIdentifier> getPaoMultiPointIdentifiers(Iterable<? extends YukonPao> devices,
                                                                      Set<? extends Attribute> attributes) {
        final List<PaoMultiPointIdentifier> devicesAndPoints = 
            Lists.newArrayListWithCapacity(IterableUtils.guessSize(devices));
        for (YukonPao pao : devices) {
            List<PaoPointIdentifier> points = Lists.newArrayListWithCapacity(attributes.size());
            for (Attribute attribute : attributes) {
                PaoPointIdentifier paoPointIdentifier = 
                    attributeService.getPaoPointIdentifierForNonMappedAttribute(pao, attribute);
                points.add(paoPointIdentifier);
            }
            devicesAndPoints.add(new PaoMultiPointIdentifier(points));
        }
        return devicesAndPoints;
    }
    
    @Autowired
    public void setStrategies(List<DeviceAttributeReadStrategy> strategyList) {
        Builder<DeviceAttributeReadStrategyType, DeviceAttributeReadStrategy> builder = ImmutableMap.builder();
        for (DeviceAttributeReadStrategy strategy : strategyList) {
            builder.put(strategy.getType(), strategy);
        }
        strategies = builder.build();
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

}
