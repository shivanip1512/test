package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class DeviceAttributeReadServiceImpl implements DeviceAttributeReadService {
    
    private static final LogHelper log = LogHelper.getInstance(YukonLogManager.getLogger(DeviceAttributeReadServiceImpl.class));
    
    private AttributeService attributeService;
    
    private ImmutableMap<StrategyType, DeviceAttributeReadStrategy> strategies = ImmutableMap.of();
    
    @Override
    public boolean isReadable(Iterable<? extends YukonPao> devices, Set<Attribute> attributes,
                              LiteYukonUser user) {

        log.debug("isReadable called for %.3s and %s", devices, attributes);

        ImmutableMultimap<PaoType, ? extends YukonPao> paoTypes = PaoUtils.mapPaoTypes(devices);

        // loop through each PaoType and strategy
        for (PaoType paoType : paoTypes.keySet()) {
            for (StrategyType strategy : strategies.keySet()) {
                DeviceAttributeReadStrategy impl = strategies.get(strategy);
                if (impl.canRead(paoType)) {
                    ImmutableCollection<? extends YukonPao> immutableCollection = paoTypes.get(paoType);
                    List<PaoMultiPointIdentifier> devicesAndPoints = attributeService.findPaoMultiPointIdentifiersForNonMappedAttributes(immutableCollection, attributes);
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
        
        log.debug("initiateRead of %s called for %.3s and %s", type, devices, attributes);
        
        // this will represent the "plan" for how all of the input devices will be read
        Map<StrategyType, Collection<PaoMultiPointIdentifier>> thePlan = 
            Maps.newEnumMap(StrategyType.class);
        
        // we need to resolve the attributes first to figure out which physical devices we'll be reading
        final List<PaoMultiPointIdentifier> devicesAndPoints = attributeService.findPaoMultiPointIdentifiersForNonMappedAttributes(devices, attributes);

        Multimap<PaoType, PaoMultiPointIdentifier> byPhysicalPaoType = ArrayListMultimap.create(1, devicesAndPoints.size());
        for (PaoMultiPointIdentifier multiPoints : devicesAndPoints) {
            byPhysicalPaoType.put(multiPoints.getPao().getPaoType(), multiPoints);
        }
        
        // loop through each PaoType and strategy, add applicable PaoIdentifiers to the plan
        for (PaoType paoType : byPhysicalPaoType.keySet()) {
            StrategyType foundStrategy = null;
            for (StrategyType strategy : strategies.keySet()) {
                DeviceAttributeReadStrategy impl = strategies.get(strategy);
                if (impl.canRead(paoType)) {
                    foundStrategy = strategy;
                    break;
                }
            }
            Collection<PaoMultiPointIdentifier> paoPointIdentifiersForType = byPhysicalPaoType.get(paoType);
            if (foundStrategy == null) {
                log.debug("no strategy found for %s devices: %.7s", paoType, paoPointIdentifiersForType);
                for (PaoMultiPointIdentifier paoMultiPoints : paoPointIdentifiersForType) {
                    MessageSourceResolvable summary = 
                        YukonMessageSourceResolvable.createDefaultWithoutCode("no strategy for " + paoType);
                    DeviceAttributeReadError strategyError = 
                        new DeviceAttributeReadError(DeviceAttributeReadErrorType.NO_STRATEGY, summary);
                    delegateCallback.receivedError(paoMultiPoints.getPao(), strategyError);
                }
            } else {
                log.debug("strategy found for %d %s devices", paoPointIdentifiersForType.size(), paoType);
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
                log.debug("one strategy for read is complete, %d remaining", count);
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
        
        for (StrategyType strategy : thePlan.keySet()) {
            DeviceAttributeReadStrategy impl = strategies.get(strategy);
            Collection<PaoMultiPointIdentifier> devicesForThisStrategy = thePlan.get(strategy);
            
            impl.initiateRead(devicesForThisStrategy, strategyCallback, type, user);
        }
    }

    @Autowired
    public void setStrategies(List<DeviceAttributeReadStrategy> strategyList) {
        Builder<StrategyType, DeviceAttributeReadStrategy> builder = ImmutableMap.builder();
        for (DeviceAttributeReadStrategy strategy : strategyList) {
            builder.put(strategy.getType(), strategy);
        }
        strategies = builder.build();
        log.debug("supported strategies: %s", strategies.keySet());
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

}
