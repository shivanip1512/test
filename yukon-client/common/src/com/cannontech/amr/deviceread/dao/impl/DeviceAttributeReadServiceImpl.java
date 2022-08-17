package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.dao.WaitableDeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.service.DeviceReadResult;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionInput;
import com.cannontech.common.bulk.collection.device.model.CollectionActionLogDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.bulk.collection.device.service.CollectionActionCancellationService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifierWithUnsupported;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class DeviceAttributeReadServiceImpl implements DeviceAttributeReadService, CollectionActionCancellationService  {

    private static final Logger log = YukonLogManager.getLogger(DeviceAttributeReadServiceImpl.class);

    @Autowired private AttributeService attributeService;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private List<DeviceAttributeReadStrategy> strategies;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private CollectionActionService collectionActionService;
        
	@Override
	public boolean isReadable(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes,
			LiteYukonUser user) {

		if (log.isDebugEnabled()) {
			log.debug("isReadable Attributes:" + attributes + " Devices:" + devices);
		}
		
		if(attributes.isEmpty()) {
		    return false;
		}

        Map<PaoType, List<PaoMultiPointIdentifier>> devicesAndPoints =
            attributeService.findPaoMultiPointIdentifiersForAttributes(devices, attributes).stream()
            .collect(Collectors.groupingBy(p -> p.getPao().getPaoType()));

        for (YukonPao pao : devices) {
            Optional<DeviceAttributeReadStrategy> strategy =
                strategies.stream().filter(s -> s.canRead(pao.getPaoIdentifier().getPaoType())).findFirst();
            if (strategy.isPresent()
                && strategy.get().isReadable(devicesAndPoints.get(pao.getPaoIdentifier().getPaoType()))) {
                return true;
            }
        }
        return false;

	}

    @Override
    public void initiateRead(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes,
            DeviceAttributeReadCallback callback, DeviceRequestType type, LiteYukonUser user) {

        if (log.isDebugEnabled()) {
            log.debug("initiateRead  Type:" + type + " Attributes:" + attributes + " Devices:" + devices);
        }

        DeviceCollectionSummary deviceCollectionSummary = new DeviceCollectionSummary(devices, attributes, user);

        int requestCount = deviceCollectionSummary.getRequestCount();
        CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE, type, requestCount, user);

        if (log.isDebugEnabled()) {
            log.debug("creId  " + execution.getId());
        }

        Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> pointsForStrategy =
            deviceCollectionSummary.getPointsForStrategy();
       
        DeviceAttributeReadCallback strategyCallback = new DeviceAttributeReadCallback() {
            
            List<StrategyType> types = Collections.synchronizedList(pointsForStrategy.keys().stream()
                    .map(s -> s.getStrategy()).collect(Collectors.toList()));
            
            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                callback.receivedValue(pao, value);
            }

            @Override
            public void receivedLastValue(PaoIdentifier pao, String value) {
                callback.receivedLastValue(pao, value);
            }

            @Override
            public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
                callback.receivedError(pao, error);
            }

            @Override
            public void receivedException(SpecificDeviceErrorDescription error) {
                callback.receivedException(error);
            }

            @Override
            public void complete(StrategyType type) {
                log.debug("Completed strategy=" + type + " remaining=" + types);
                types.remove(type);
                if (types.isEmpty()) {
                    log.debug("All strategies complete");
                    complete();
                }
            }

            @Override
            public void complete() {
                completeExecution(execution);
                callback.complete();
            }
        };
           
        for (PaoIdentifier pao : deviceCollectionSummary.getErrors().keySet()) {
            strategyCallback.receivedError(pao, deviceCollectionSummary.getErrors().get(pao));
        }

        if (pointsForStrategy.isEmpty()) {
            callback.complete();
        } else {
            for (DeviceAttributeReadStrategy strategy : pointsForStrategy.keySet()) {
                strategy.initiateRead(pointsForStrategy.get(strategy), strategyCallback, execution, user);
            }
        }
        commandRequestExecutionResultDao.saveUnsupported(deviceCollectionSummary.getUnsupportedDevices(),
            execution.getId(), CommandRequestUnsupportedType.UNSUPPORTED);
    }

    @Override
    public int initiateRead(DeviceCollection deviceCollection, Set<? extends Attribute> attributes,
            DeviceRequestType type, SimpleCallback<CollectionActionResult> callback, YukonUserContext context) {

        DeviceCollectionSummary deviceCollectionSummary =
            new DeviceCollectionSummary(deviceCollection.getDeviceList(), attributes, context.getYukonUser());

        Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> pointsForStrategy =
            deviceCollectionSummary.getPointsForStrategy();

        CollectionActionResult result = collectionActionService.createResult(CollectionAction.READ_ATTRIBUTE,
            getInputs(attributes, context), deviceCollection, CommandRequestType.DEVICE, type, context);

        DeviceAttributeReadCallback strategyCallback = new DeviceAttributeReadCallback() {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
            Set<StrategyType> types = Collections.synchronizedSet(
                pointsForStrategy.keys().stream().map(s -> s.getStrategy()).collect(Collectors.toSet()));

            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                CollectionActionLogDetail detail = new CollectionActionLogDetail(pao);
                detail.setValue(value);
                result.appendToLogWithoutAddingToGroup(detail);
            }
            @Override
            public void receivedLastValue(PaoIdentifier pao, String value) {
                CollectionActionLogDetail detail = new CollectionActionLogDetail(pao, CollectionActionDetail.SUCCESS);
                detail.setLastValue(value);
                result.addDeviceToGroup(CollectionActionDetail.SUCCESS, pao, detail);
            }

            @Override
            public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
                CollectionActionLogDetail detail = new CollectionActionLogDetail(pao, CollectionActionDetail.FAILURE);
                detail.setDeviceErrorText(accessor.getMessage(error.getDetail()));
                result.addDeviceToGroup(CollectionActionDetail.FAILURE, pao, detail);
            }

            @Override
            public void receivedException(SpecificDeviceErrorDescription error) {
                result.setExecutionExceptionText(accessor.getMessage(error.getSummary()));
                collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
            }
            
            @Override
            public CollectionActionResult getResult() {
                return result;
            }

            @Override
            public void complete(StrategyType type) {
                if (!result.isComplete()) {
                    types.remove(type);
                    if (types.isEmpty()) {
                        complete();
                    }
                }
            }

            @Override
            public void complete() {
                if (!result.isComplete()) {
                    collectionActionService.updateResult(result, !result.isCanceled()
                        ? CommandRequestExecutionStatus.COMPLETE : CommandRequestExecutionStatus.CANCELLED);
                    if (callback != null) {
                        try {
                            callback.handle(result);
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                }
            }
        };

        if (pointsForStrategy.isEmpty()) {
            strategyCallback.complete();
        } else {
            for (DeviceAttributeReadStrategy strategy : pointsForStrategy.keySet()) {
                Iterable<PaoMultiPointIdentifier> points = pointsForStrategy.get(strategy);
                strategy.initiateRead(points, strategyCallback, result.getExecution(), context.getYukonUser());
            }
        }
        int requestCount = deviceCollectionSummary.getRequestCount();
        result.getExecution().setRequestCount(requestCount);
        commandRequestExecutionDao.saveOrUpdate(result.getExecution());
        new ArrayList<>(deviceCollectionSummary.getUnsupportedDevices());
        collectionActionService.addUnsupportedToResult(CollectionActionDetail.UNSUPPORTED, result,
            new ArrayList<>(deviceCollectionSummary.getUnsupportedDevices()));
        log.debug("updating request count =" + requestCount);
        commandRequestExecutionDao.saveOrUpdate(result.getExecution());
        return result.getCacheKey();
    }

    private LinkedHashMap<String, String> getInputs(Set<? extends Attribute> attributes, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        List<String> attributeStrings =
            attributes.stream().map(attribute -> accessor.getMessage(attribute)).collect(Collectors.toList());
        LinkedHashMap<String, String> inputs = new LinkedHashMap<>();
        inputs.put(CollectionActionInput.ATTRIBUTES.name(), String.join(", ", attributeStrings));
        return inputs;
    }
    
	@Override
	public DeviceReadResult initiateReadAndWait(YukonDevice device, Set<? extends Attribute> toRead,
			DeviceRequestType requestType, LiteYukonUser user) {
		DeviceReadResult result = new DeviceReadResult();
        WaitableDeviceAttributeReadCallback callback = new WaitableDeviceAttributeReadCallback() {

            @Override
            public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
                result.addError(error);
            }

            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                result.addPointValue(value);
            }

            @Override
            public void receivedLastValue(PaoIdentifier pao, String value) {
                result.setLastResultString(value);
            }

            @Override
            public void receivedException(SpecificDeviceErrorDescription error) {
                result.addError(error);
            }

            @Override
            public void complete() {
                result.setTimeout(false);
                super.complete();
            }

        };

		Set<YukonDevice> meterSingleton = Collections.singleton(device);
		initiateRead(meterSingleton, toRead, callback, requestType, user);
		try {
			callback.waitForCompletion();
		} catch (InterruptedException e) {
		}
		if (result.isTimeout()) {
            DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.TIMEOUT);
            MessageSourceResolvable detail =
                YukonMessageSourceResolvable.createSingleCode("yukon.common.device.attributeRead.general.timeout");
            SpecificDeviceErrorDescription error = new SpecificDeviceErrorDescription(errorDescription, detail);
            result.addError(error);
		}
		return result;
	}


	private void completeExecution(CommandRequestExecution execution) {
	    if (execution.getCommandRequestExecutionStatus() == CommandRequestExecutionStatus.STARTED) {
            log.debug("All strategies complete");
            execution.setStopTime(new Date());
            execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
            commandRequestExecutionDao.saveOrUpdate(execution);
        }
	}

	private class DeviceCollectionSummary {

        private Set<YukonPao> unsupportedDevices = new HashSet<>();
        private Set<YukonPao> supportedDevices = new HashSet<>();
        private Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> pointsForStrategy = ArrayListMultimap.create();
        private int requestCount = 0;
        private Map<PaoIdentifier, SpecificDeviceErrorDescription> errors =
            new HashMap<>();

        public DeviceCollectionSummary(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes,
                LiteYukonUser user) {

            PaoMultiPointIdentifierWithUnsupported paoPointIdentifiers =
                attributeService.findPaoMultiPointIdentifiersForAttributesWithUnsupported(devices, attributes);

            Multimap<PaoIdentifier, PaoMultiPointIdentifier> paoToPoints = ArrayListMultimap.create();

            for (PaoMultiPointIdentifier multiPoints : paoPointIdentifiers.getSupportedDevicesAndPoints()) {
                paoToPoints.put(multiPoints.getPao(), multiPoints);
            }
            for (YukonPao pao : devices) {
                Collection<PaoMultiPointIdentifier> points = paoToPoints.get(pao.getPaoIdentifier());
                boolean hasStrategy = false;
                for (DeviceAttributeReadStrategy strategy : strategies) {
                    if (strategy.canRead(pao.getPaoIdentifier().getPaoType())) {
                        hasStrategy = true;
                        if (strategy.isReadable(points)) {
                            pointsForStrategy.putAll(strategy, points);
                            supportedDevices.add(pao);
                            break;
                        } else {
                            /*
                             * Device should be counted as as "unsupported" when there is no minimal command set that
                             * covers all attributes selected for the device
                             */
                            unsupportedDevices.add(pao);
                            if (log.isDebugEnabled()) {
                                log.debug("Unsupported device(no command):" + pao);
                            }
                            addSpecificDeviceErrorDescriptionError(
                                "yukon.common.device.attributeRead.general.noCommand", pao.getPaoIdentifier());
                        }
                    }
                }
                if (!hasStrategy) {
                    unsupportedDevices.add(pao);
                    if (log.isDebugEnabled()) {
                        log.debug("Unsupported device(no strategy):" + pao);
                    }
                    addSpecificDeviceErrorDescriptionError("yukon.common.device.attributeRead.general.noStrategy",
                        pao.getPaoIdentifier());
                }
            }
                  
            for (YukonPao unsupported : paoPointIdentifiers.getUnsupportedDevices()) {
                if (!unsupportedDevices.contains(unsupported) && !supportedDevices.contains(unsupported)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Unsupported device(appribute is not supported):" + unsupported);
                    }
                    unsupportedDevices.add(unsupported);
                    addSpecificDeviceErrorDescriptionError("yukon.common.device.attributeRead.general.noAttribute",
                        unsupported.getPaoIdentifier());
                }
            }
            
            //request count that would be send to a device
            if (!pointsForStrategy.isEmpty()) {
                for (DeviceAttributeReadStrategy strategy : pointsForStrategy.keySet()) {
                    requestCount += strategy.getRequestCount(pointsForStrategy.get(strategy));
                }
            }
        }
        
        private void addSpecificDeviceErrorDescriptionError(String code, PaoIdentifier pao) {
            DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.INVALID_ACTION);
            MessageSourceResolvable detail =
                YukonMessageSourceResolvable.createSingleCodeWithArguments(code, pao.getPaoIdentifier().getPaoType());
            SpecificDeviceErrorDescription error = new SpecificDeviceErrorDescription(errorDescription, detail, detail);
            errors.put(pao, error);
        }
     
        public Set<YukonPao> getUnsupportedDevices() {
            return unsupportedDevices;
        }
                
        public Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> getPointsForStrategy(){
            return pointsForStrategy;
        }
        
        public Map<PaoIdentifier, SpecificDeviceErrorDescription> getErrors() {
            return errors;
        }

        public int getRequestCount() {
            return requestCount;
        }
    }

    @Override
    public boolean isCancellable(CollectionAction action) {
        return action == CollectionAction.READ_ATTRIBUTE || action == CollectionAction.SEND_COMMAND;
    }

    @Override
    public void cancel(int key, LiteYukonUser user) {
        CollectionActionResult result = collectionActionService.getResult(key);
        if (result.isCancelable()) {
            result.setCanceled(true);
            collectionActionService.updateResult(result, CommandRequestExecutionStatus.CANCELING);
            strategies.forEach(strategy -> strategy.cancel(result, user));
        }
    }
}
