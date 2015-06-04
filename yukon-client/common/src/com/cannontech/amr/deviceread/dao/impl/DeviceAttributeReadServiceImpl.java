package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.dao.WaitableDeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.service.DeviceReadResult;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifierWithUnsupported;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class DeviceAttributeReadServiceImpl implements DeviceAttributeReadService {

    private static final Logger log = YukonLogManager.getLogger(DeviceAttributeReadServiceImpl.class);

    @Autowired private AttributeService attributeService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private List<DeviceAttributeReadStrategy> strategies;
    @Autowired private DeviceAttributeReadPlcStrategy plcStrategy;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    
    private final RecentResultsCache<GroupMeterReadResult> resultsCache = new RecentResultsCache<>();
    
	@Override
	public boolean isReadable(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes,
			LiteYukonUser user) {

		if (log.isDebugEnabled()) {
			log.debug("isReadable Attributes:" + attributes + " Devices:" + devices);
		}

		List<PaoMultiPointIdentifier> devicesAndPoints = attributeService.findPaoMultiPointIdentifiersForAttributes(
				devices, attributes);

		Multimap<PaoType, PaoMultiPointIdentifier> pointsByPaoType = ArrayListMultimap.create(1,
				devicesAndPoints.size());
		for (PaoMultiPointIdentifier multiPoints : devicesAndPoints) {
			pointsByPaoType.put(multiPoints.getPao().getPaoType(), multiPoints);
		}

		for (YukonPao pao : devices) {
			for (DeviceAttributeReadStrategy strategy : strategies) {
				if (strategy.canRead(pao.getPaoIdentifier().getPaoType())) {
					Collection<PaoMultiPointIdentifier> points = pointsByPaoType.get(pao.getPaoIdentifier()
							.getPaoType());
					boolean readable = strategy.isReadable(points, user);
					if (readable) {
						return true;
					}
				}
			}
		}
		return false;

	}

    @Override
    public void initiateRead(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes,
            DeviceAttributeReadCallback delegateCallback, DeviceRequestType type, LiteYukonUser user) {

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
        DeviceAttributeReadStrategyCallback strategyCallback =
            new ReadCallback(delegateCallback, pointsForStrategy.keySet().size(), execution);

        for (PaoIdentifier pao : deviceCollectionSummary.getErrors().keySet()) {
            strategyCallback.receivedError(pao, deviceCollectionSummary.getErrors().get(pao));
        }

        if (pointsForStrategy.isEmpty()) {
            strategyCallback.complete();
        } else {
            for (DeviceAttributeReadStrategy strategy : pointsForStrategy.keySet()) {
                strategy.initiateRead(pointsForStrategy.get(strategy), strategyCallback, type, execution, user);
            }
        }
        commandRequestExecutionResultDao.saveUnsupported(deviceCollectionSummary.getUnsupportedDevices(),
            execution.getId(), CommandRequestUnsupportedType.UNSUPPORTED);
    }

    @Override
    public String initiateRead(DeviceCollection deviceCollection, Set<? extends Attribute> attributes,
            DeviceRequestType type, SimpleCallback<GroupMeterReadResult> callback, LiteYukonUser user) {

        
        if (log.isDebugEnabled()) {
            log.debug("initiateRead  Type:" + type + " Attributes:" + attributes + " Devices:"
                + deviceCollection.getDeviceList());
        }

        DeviceCollectionSummary deviceCollectionSummary =
            new DeviceCollectionSummary(deviceCollection.getDeviceList(), attributes, user);

        Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> pointsForStrategy =
            deviceCollectionSummary.getPointsForStrategy();
        int requestCount = deviceCollectionSummary.getRequestCount();

        CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE, type, requestCount, user);
        CommandRequestExecutionIdentifier identifier = new CommandRequestExecutionIdentifier(execution.getId());

        if (log.isDebugEnabled()) {
            log.debug("creId  " + execution.getId());
        }

        StoredDeviceGroup originalDeviceCollectionCopyGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(originalDeviceCollectionCopyGroup, deviceCollection.getDeviceList());
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup();
        StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup();
        StoredDeviceGroup unsupportedGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(unsupportedGroup, deviceCollectionSummary.getUnsupportedDevices());

        DeviceCollection successCollection = deviceGroupCollectionHelper.buildDeviceCollection(successGroup);
        DeviceCollection failureCollection = deviceGroupCollectionHelper.buildDeviceCollection(failureGroup);
        DeviceCollection unsupportedCollection = deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup);
        DeviceCollection originalDeviceCollectionCopy =
            deviceGroupCollectionHelper.buildDeviceCollection(originalDeviceCollectionCopyGroup);

        GroupMeterReadResult result = new GroupMeterReadResult();
        GroupCallback groupCallback =
            new GroupCallback(callback, execution, successGroup, failureGroup, result,
                pointsForStrategy.keySet().size());
        result.setResultHolder(groupCallback);
        result.setCallback(groupCallback);
        result.setCommandRequestExecutionIdentifier(identifier);
        result.setAttributes(attributes);
        result.setCommandRequestExecutionType(type);
        result.setDeviceCollection(deviceCollection);
        result.setSuccessCollection(successCollection);
        result.setFailureCollection(failureCollection);
        result.setUnsupportedCollection(unsupportedCollection);
        result.setOriginalDeviceCollectionCopy(originalDeviceCollectionCopy);
        result.setStartTime(new Date());

        String key = resultsCache.addResult(result);
        result.setKey(key);

        if (pointsForStrategy.isEmpty()) {
            groupCallback.complete();
        } else {
            for (DeviceAttributeReadStrategy strategy : pointsForStrategy.keySet()) {
                strategy.initiateRead(pointsForStrategy.get(strategy), groupCallback, type, user);
            }
        }

        commandRequestExecutionResultDao.saveUnsupported(deviceCollectionSummary.getUnsupportedDevices(),
            execution.getId(), CommandRequestUnsupportedType.UNSUPPORTED);
        return key;
    }

	@Override
	public DeviceReadResult initiateReadAndWait(YukonDevice device, Set<? extends Attribute> toRead,
			DeviceRequestType requestType, LiteYukonUser user) {
		DeviceReadResult result = new DeviceReadResult();
		SingleDeviceReadCallback callback = new SingleDeviceReadCallback(result);
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

	private class GroupCallback extends GroupCommandCompletionCallback {

		AtomicInteger completionCounter;
		StoredDeviceGroup successGroup;
		StoredDeviceGroup failureGroup;
		SimpleCallback<GroupMeterReadResult> callback;
		GroupMeterReadResult result;

		GroupCallback(SimpleCallback<GroupMeterReadResult> callback, CommandRequestExecution execution,
				StoredDeviceGroup successGroup, StoredDeviceGroup failureGroup, GroupMeterReadResult result,
				int strategyCount) {
			super.setExecution(execution);
			this.successGroup = successGroup;
			this.failureGroup = failureGroup;
			this.callback = callback;
			this.result = result;
			completionCounter = new AtomicInteger(strategyCount);
		}

		@Override
		public void complete() {
			try {
				if (log.isDebugEnabled()) {
					log.debug("Remaining strategies:" + completionCounter.get());
				}
				int count = completionCounter.decrementAndGet();
				if (count <= 0) {
					super.complete();
					if (execution.getCommandRequestExecutionStatus() == CommandRequestExecutionStatus.STARTED) {
						log.debug("All startegies complete");
						execution.setStopTime(new Date());
						execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
						commandRequestExecutionDao.saveOrUpdate(execution);
					}
					callback.handle(result);
				}

			} catch (Exception e) {
				log.debug("There was an error executing the callback", e);
			}
		}

		@Override
		public void handleSuccess(SimpleDevice device) {
			deviceGroupMemberEditorDao.addDevices(successGroup, device);
		}

		@Override
		public void handleFailure(SimpleDevice device) {
			deviceGroupMemberEditorDao.addDevices(failureGroup, device);
		}
	}

	public class SingleDeviceReadCallback extends WaitableDeviceAttributeReadCallback {

		DeviceReadResult result;

		SingleDeviceReadCallback(DeviceReadResult result) {
			this.result = result;
		}

		@Override
		public void complete() {
			result.setTimeout(false);
			super.complete();
		}
		
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
		public void receivedException(SpecificDeviceErrorDescription exception) {
			result.addError(exception);
		}

	}

	private class ReadCallback implements DeviceAttributeReadStrategyCallback {

		AtomicInteger completionCounter;
		DeviceAttributeReadCallback delegateCallback;
		CommandRequestExecution execution;

		ReadCallback(DeviceAttributeReadCallback delegateCallback, int strategyCount, CommandRequestExecution execution) {
			completionCounter = new AtomicInteger(strategyCount);
			this.delegateCallback = delegateCallback;
			this.execution = execution;
		}

		@Override
		public void complete() {
			try {
				if (log.isDebugEnabled()) {
					log.debug("Remaining strategies:" + completionCounter.get());
				}
				int count = completionCounter.decrementAndGet();
				if (count <= 0) {
					if (execution.getCommandRequestExecutionStatus() == CommandRequestExecutionStatus.STARTED) {
						log.debug("All startegies complete");
						execution.setStopTime(new Date());
						execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
						commandRequestExecutionDao.saveOrUpdate(execution);
					}
					delegateCallback.complete();
				}

			} catch (Exception e) {
				log.debug("There was an error executing the callback", e);
			}
		}

		@Override
		public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
			delegateCallback.receivedError(pao, error);
		}

		@Override
		public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
			delegateCallback.receivedValue(pao, value);
		}

		@Override
		public void receivedLastValue(PaoIdentifier pao, String value) {
			delegateCallback.receivedLastValue(pao, value);
		}

		@Override
		public void receivedException(SpecificDeviceErrorDescription exception) {
			delegateCallback.receivedException(exception);
		}
	};

	@Override
	public List<GroupMeterReadResult> getCompleted() {
		return resultsCache.getCompleted();
	}

	@Override
	public List<GroupMeterReadResult> getCompletedByType(DeviceRequestType type) {
		List<GroupMeterReadResult> completed = getCompleted();
		return filterByType(completed, type);
	}

	@Override
	public List<GroupMeterReadResult> getPending() {
		return resultsCache.getPending();
	}

	@Override
	public List<GroupMeterReadResult> getPendingByType(DeviceRequestType type) {
		List<GroupMeterReadResult> pending = getPending();
		return filterByType(pending, type);
	}

	@Override
	public GroupMeterReadResult getResult(String id) {
		return resultsCache.getResult(id);
	}

	private List<GroupMeterReadResult> filterByType(List<GroupMeterReadResult> pending, DeviceRequestType type) {
		List<GroupMeterReadResult> pendingOfType = new ArrayList<>();
		for (GroupMeterReadResult result : pending) {
			if (result.getCommandRequestExecutionType().equals(type)) {
				pendingOfType.add(result);
			}
		}
		return pendingOfType;
	}
	
	private class DeviceCollectionSummary {

        private Set<YukonPao> unsupportedDevices = new HashSet<YukonPao>();
        private Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> pointsForStrategy = ArrayListMultimap.create();
        private int requestCount = 0;
        private Map<PaoIdentifier, SpecificDeviceErrorDescription> errors =
            new HashMap<PaoIdentifier, SpecificDeviceErrorDescription>();

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
                        if (strategy.isReadable(points, user)) {
                            pointsForStrategy.putAll(strategy, points);
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
                if (!unsupportedDevices.contains(unsupported)) {
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
}
