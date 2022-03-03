package com.cannontech.amr.demandreset.service.impl;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetCallback.Results;
import com.cannontech.amr.demandreset.service.DemandResetStrategyService;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetReply;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetReplyType;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetRequest;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.jms.JmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.RfnDemandResetState;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DemandResetRfnServiceImpl implements DemandResetStrategyService, PointDataListener {
    private static final Logger log = YukonLogManager.getLogger(DemandResetRfnServiceImpl.class);
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private ScheduledExecutorService executor = null;
    private RequestReplyTemplateImpl<RfnMeterDemandResetReply> qrTemplate;
    private Duration verificationTimeout;
    private static DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT);

    private class DeviceVerificationInfo {
        DemandResetCallback callback;
        Instant whenRequested;
        CommandRequestExecution verificationExecution;
        Map<Integer, SimpleDevice> pointToDevice = new ConcurrentHashMap<>();  

        DeviceVerificationInfo(DemandResetCallback callback, CommandRequestExecution verificationExecution) {
            this.callback = callback;
            this.whenRequested = new Instant();
            this.verificationExecution = verificationExecution;
        }
        
        void addDevice(int pointId, SimpleDevice device){
            pointToDevice.put(pointId, device);
        }
        
        SimpleDevice getDevice(int pointId){
            return pointToDevice.get(pointId);
        }
        
        void removeDevice(int pointId){
            pointToDevice.remove(pointId);
        }
        
        // returns true if no devices left to verify
        boolean isAllVerified() {
            return pointToDevice.isEmpty();
        }
        
        Set<Integer> getPointIds(){
            return pointToDevice.keySet();
        }

        private DemandResetRfnServiceImpl getOuterType() {
            return DemandResetRfnServiceImpl.this;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((whenRequested == null) ? 0 : whenRequested.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            DeviceVerificationInfo other = (DeviceVerificationInfo) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (whenRequested == null) {
                if (other.whenRequested != null) {
                    return false;
                }
            } else if (!whenRequested.equals(other.whenRequested)) {
                return false;
            }
            return true;
        }
    }
    
    private final Set<DeviceVerificationInfo> devicesAwaitingVerification = Collections
        .synchronizedSet(new HashSet<DeviceVerificationInfo>());

    @Override
    public void cancel(CollectionActionResult result, LiteYukonUser user) {
        synchronized (devicesAwaitingVerification) {
            Iterator<DeviceVerificationInfo> infoIterator = devicesAwaitingVerification.iterator();
            while (infoIterator.hasNext()) {
                DeviceVerificationInfo verificationInfo = infoIterator.next();
                // find devices that are waiting for verification with the same command request execution id
                // as execution we are attempting to cancel
                if (verificationInfo.verificationExecution.getId().equals(result.getVerificationExecution().getId())) {
                    //remove devices from the waiting list
                    infoIterator.remove();
                    //mark devices as canceled
                    log.debug("Proccessing cancellations for RFN meters: {}. RF Cancel Complete.", verificationInfo.pointToDevice.values());             
                    //unregister devices
                    asyncDynamicDataSource.unRegisterForPointData(DemandResetRfnServiceImpl.this,
                        verificationInfo.pointToDevice.keySet());
                }
            }
        }
        // doesn't support cancellation
        result.getCancellationCallbacks(getStrategy()).forEach(callback -> {
            callback.cancel();
        });
    }
    
    private class TimeoutChecker implements Runnable {
        @Override
        public void run() {
            synchronized (devicesAwaitingVerification) {
                Instant now = new Instant();
                try {
                    Iterator<DeviceVerificationInfo> infoIterator= devicesAwaitingVerification.iterator();
                    while (infoIterator.hasNext()) {
                        DeviceVerificationInfo verificationInfo = infoIterator.next();
                        DemandResetCallback callback = verificationInfo.callback;
                        Instant timeout = verificationInfo.whenRequested.plus(verificationTimeout);
                   
                        log.debug("[TimeoutChecker] Requested on: {} devices: {} timed out: {}",
                                verificationInfo.whenRequested.toDate(), verificationInfo.pointToDevice.values(),
                                timeFormatter.format(timeout.toDate()));

                        if (timeout.isBefore(now)) {
                            Iterator<Entry<Integer, SimpleDevice>> deviceIterator =
                                verificationInfo.pointToDevice.entrySet().iterator();
                            while (deviceIterator.hasNext()) {
                                Entry<Integer, SimpleDevice> entry = deviceIterator.next();
                                SimpleDevice device = entry.getValue();
                                int pointId = entry.getKey();

                                log.debug("[TimeoutChecker] device: {} timed out waiting for point from Network Manager.",
                                        device);
                                
                                callback.cannotVerify(device, getError(DeviceError.NM_TIMEOUT));
                                deviceIterator.remove();
                                asyncDynamicDataSource.unRegisterForPointData(DemandResetRfnServiceImpl.this,
                                                                              ImmutableSet.of(pointId));
                                commandRequestExecutionResultDao.saveCommandRequestExecutionResult(
                                    verificationInfo.verificationExecution, device.getDeviceId(), DeviceError.NM_TIMEOUT.getCode());
                            }

                        }
                        if (verificationInfo.isAllVerified()) {
                            infoIterator.remove();
                            callback.complete(StrategyType.NM);
                            log.debug("[TimeoutChecker] Requested on: {}. All devices verified. NM strategy completed."
                                    , verificationInfo.whenRequested.toDate());
                        }
                    }
                } catch (Exception e) {
                    // Exception in this method with cause the TimeoutChecker to be terminated.
                    // Example: synchronization problem, if exception is ignored, on the next run the
                    // devices will timeout and the code will work correctly.
                    // The exception should not happen. To test: run demand reset on the same group
                    // of devices multiple times with a couple of seconds between each run
                    log.error("Error in TimeoutChecker", e);
                }
            }
        }
    }

    @PostConstruct
    public void initialize() {
        String configurationName = "RFN_METER_DEMAND_RESET";
        YukonJmsTemplate jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RFN_METER_DEMAND_RESET);
        qrTemplate = new RequestReplyTemplateImpl<>(configurationName, configurationSource, jmsTemplate);
        verificationTimeout = configurationSource.getDuration(configurationName
            + "_VALIDATION_TIMEOUT", Duration.standardHours(26));
    }

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        Set<T> devicesOfCorrectType =
                paoDefinitionDao.filterPaosForTag(devices, PaoTag.RFN_DEMAND_RESET);
        final Map<? extends YukonPao, RfnIdentifier> meterIdentifiersByPao =
                rfnDeviceDao.getRfnIdentifiersByPao(devicesOfCorrectType);

        Predicate<YukonPao> predicate = new Predicate<YukonPao>() {
            @Override
            public boolean apply(YukonPao device) {
                return meterIdentifiersByPao.containsKey(device);
            }
        };

        return Sets.filter(devices, predicate);
    }
        
    @Override
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos){
        BiMap<PaoIdentifier, LitePoint> deviceToPoint =
            attributeService.getPoints(paos, BuiltInAttribute.RF_DEMAND_RESET_STATUS);
        return Sets.newHashSet(PaoUtils.asSimpleDeviceList(deviceToPoint.keySet()));
    }

    @Override
    public void sendDemandReset(final CommandRequestExecution sendExecution,
                                final Set<? extends YukonPao> paos, final DemandResetCallback callback,
                                LiteYukonUser user) {
        Map<? extends YukonPao, RfnIdentifier> meterIdentifiersByPao =
                rfnDeviceDao.getRfnIdentifiersByPao(paos);
        final Map<SimpleDevice, SpecificDeviceErrorDescription> errors = Maps.newHashMap();
        final Map<RfnIdentifier, SimpleDevice> devicesByRfnMeterIdentifier = Maps.newHashMap();

        final Set<SimpleDevice> verifiableDevices = getVerifiableDevices(paos);
        Set<SimpleDevice> devicesWithoutPoint = Sets.difference(Sets.newHashSet(PaoUtils.asSimpleDeviceListFromPaos(paos)), verifiableDevices);
        
        if(!devicesWithoutPoint.isEmpty()){
            log.error("Can't Verify:" + devicesWithoutPoint + " \"RF Demand Reset Status\" point is missing.");
        }
        for (YukonPao device : devicesWithoutPoint) {
            callback.cannotVerify(new SimpleDevice(device), getError(DeviceError.NO_POINT));
        }
        
        for (Map.Entry<? extends YukonPao, RfnIdentifier> entry : meterIdentifiersByPao.entrySet()) {
            YukonPao pao = entry.getKey();
            RfnIdentifier rfnMeterIdentifier = entry.getValue();
            devicesByRfnMeterIdentifier.put(rfnMeterIdentifier, new SimpleDevice(pao));
        }

        JmsReplyHandler<RfnMeterDemandResetReply> handler =
            new JmsReplyHandler<RfnMeterDemandResetReply>() {
                @Override
                public void complete() {
                    callback.initiated(new Results(paos, errors), getStrategy());
                }

                @Override
                public Class<RfnMeterDemandResetReply> getExpectedType() {
                    return RfnMeterDemandResetReply.class;
                }

                @Override
                public void handleException(Exception e) {
                    callback
                        .processingExceptionOccurred("There was an error sending the command demand reset command for RFN devices.");

                }

                @Override
                public void handleReply(RfnMeterDemandResetReply statusReply) {
                    Map<RfnIdentifier, RfnMeterDemandResetReplyType> replyTypes = statusReply.getReplyTypes();
                    Collection<RfnIdentifier> rfnMeters = replyTypes.keySet();
                    for (RfnIdentifier rfnMeterIdentifier : rfnMeters) {
                        RfnMeterDemandResetReplyType replyType = replyTypes.get(rfnMeterIdentifier);
                        SimpleDevice device = devicesByRfnMeterIdentifier.get(rfnMeterIdentifier);
                        log.debug("{} RfnMeterDemandResetReply: {}", rfnMeterIdentifier, replyType);
                        if (replyType == RfnMeterDemandResetReplyType.OK) {
                            commandRequestExecutionResultDao.saveCommandRequestExecutionResult(sendExecution, device.getDeviceId(), 0);
                        } else {
                            processError(device, replyType.getErrorCode());
                        }
                    }
                }

                @Override
                public void handleTimeout() {
                    log.error("Timed out waiting for RFN demand reset");
                }

                /**
                 * The demand reset command did not make it out. The first response is not
                 * received from NM before timing out or ReplyType is not OK.
                 * 
                 * This method unregisters for point data, removes the device from the list of
                 * devices waiting for verification and creates an applicable error.
                 */
                private void processError(SimpleDevice device, int errorCode) {
                    
                commandRequestExecutionResultDao.saveCommandRequestExecutionResult(sendExecution, device.getDeviceId(), errorCode);
                errors.put(device, getError(DeviceError.getErrorByCode(errorCode)));
                    // check if this device needs to be verified
                    if (verifiableDevices.contains(device)) {

                        synchronized (devicesAwaitingVerification) {
                            Iterator<DeviceVerificationInfo> infoIterator = devicesAwaitingVerification.iterator();
                            while (infoIterator.hasNext()) {
                                DeviceVerificationInfo verificationInfo = infoIterator.next();
                                Iterator<Entry<Integer, SimpleDevice>> deviceIterator =
                                    verificationInfo.pointToDevice.entrySet().iterator();
                                while (deviceIterator.hasNext()) {
                                    Entry<Integer, SimpleDevice> entry = deviceIterator.next();
                                    if (entry.getValue().getDeviceId() == device.getDeviceId()) {
                                        int pointId = entry.getKey();
                                        deviceIterator.remove();
                                        asyncDynamicDataSource.unRegisterForPointData(DemandResetRfnServiceImpl.this,
                                                                                      ImmutableSet.of(pointId));
                                        commandRequestExecutionResultDao.saveCommandRequestExecutionResult(
                                            verificationInfo.verificationExecution, device.getDeviceId(), errorCode);
                                    }
                                }
                                if (verificationInfo.isAllVerified()) {
                                    log.debug("TimeoutChecker: All devices verified.");
                                    infoIterator.remove();
                                    callback.complete(StrategyType.NM);
                                }
                            }
                        }
                    }
                }
            };
                    
        // The set returned by keySet isn't serializable, so we have to make a copy.
        Set<RfnIdentifier> meterIds = Sets.newHashSet(devicesByRfnMeterIdentifier.keySet());
        
        commandRequestExecutionResultDao.saveExecutionRequest(sendExecution.getId(), 
                devicesByRfnMeterIdentifier.values().stream()
                    .map(device -> device.getDeviceId())
                    .collect(Collectors.toSet()));
        
        qrTemplate.send(new RfnMeterDemandResetRequest(meterIds), handler);
    }
    
    @Override
    public void sendDemandResetAndVerify(final CommandRequestExecution sendExecution, final CommandRequestExecution verificationExecution,
                                final Set<? extends YukonPao> paos, final DemandResetCallback callback,
                                LiteYukonUser user) {

        if(callback.getResult() != null) {
            callback.getResult().addCancellationCallback(
                new CollectionActionCancellationCallback(getStrategy(),  callback));
        }
        sendDemandReset(sendExecution, paos, callback, user);
        BiMap<PaoIdentifier, LitePoint> deviceToPoint =
            attributeService.getPoints(paos, BuiltInAttribute.RF_DEMAND_RESET_STATUS);
        
        final Set<SimpleDevice> verifiableDevices = Sets.newHashSet(PaoUtils.asSimpleDeviceList(deviceToPoint.keySet()));
        
        commandRequestExecutionResultDao.saveExecutionRequest(verificationExecution.getId(),
                verifiableDevices.stream()
                        .map(device -> device.getDeviceId())
                        .collect(Collectors.toSet()));
        
        if (!verifiableDevices.isEmpty()) {
            log.debug("Devices awaiting verification: {}", verifiableDevices);
            DeviceVerificationInfo deviceVerificationInfo =
                new DeviceVerificationInfo(callback, verificationExecution);
            for (SimpleDevice device : verifiableDevices) {
                deviceVerificationInfo.addDevice(deviceToPoint.get(device.getPaoIdentifier()).getLiteID(), device);
            }
            devicesAwaitingVerification.add(deviceVerificationInfo);
            asyncDynamicDataSource.registerForPointData(this, deviceVerificationInfo.getPointIds());
            setupPeriodicCheck();
        }
    }

    private synchronized void setupPeriodicCheck() {
        // Periodically check for expired devices.
        if (executor == null) {
            executor = Executors.newSingleThreadScheduledExecutor();
            // Since we're starting the executor when we get our first demand reset request, we
            // can wait the full timeout before we check the first time.  After that, we need to
            // check more often.
            executor.scheduleWithFixedDelay(new TimeoutChecker(),
                                            verificationTimeout.getStandardMinutes(), 5, TimeUnit.MINUTES);
        }
    }

    @Override
    public void pointDataReceived(PointValueQualityHolder pointData) {
        synchronized (devicesAwaitingVerification) {
            Iterator<DeviceVerificationInfo> infoIterator = devicesAwaitingVerification.iterator();
            while (infoIterator.hasNext()) {
                DeviceVerificationInfo verificationInfo = infoIterator.next();
                DemandResetCallback callback = verificationInfo.callback;
                SimpleDevice device = verificationInfo.getDevice(pointData.getId());
                if (device != null) {
                    if (pointData.getPointDataTimeStamp() == null) {
                        log.debug("Point data received. Verification failed for device {} with data {}. PointDataTimeStamp is NULL.",
                                device, pointData);
                        callback.failed(device, getError(DeviceError.NO_TIMESTAMP));
                    } else {
                        Instant resetTime = new Instant(pointData.getPointDataTimeStamp());
                        RfnDemandResetState resetState = RfnDemandResetState.values()[(int) pointData.getValue()];
                        if (resetTime.isAfter(verificationInfo.whenRequested)
                                && resetState == RfnDemandResetState.SUCCESS) {
                            callback.verified(device, pointData);
                            log.debug(
                                    "Point data received device:{} point data: {}. Verified. ResetTime: {} Requested: {} State: {}",
                                    device, pointData,
                                    timeFormatter.format(resetTime.toDate()),
                                    timeFormatter.format(verificationInfo.whenRequested.toDate()),
                                    resetState);
                        } else {
                            log.debug("Verification failed. Point data received device:{} point data: {}. ResetTime: {} Requested: {} State: {}",
                                    device, pointData,
                                    timeFormatter.format(resetTime.toDate()),
                                    timeFormatter.format(verificationInfo.whenRequested.toDate()), resetState);
                            callback.failed(device, getError(DeviceError.TIMESTAMP_OUT_OF_RANGE));
                        }
                    }
                    verificationInfo.removeDevice(pointData.getId());
                    asyncDynamicDataSource.unRegisterForPointData(this, ImmutableSet.of(pointData.getId()));
                    commandRequestExecutionResultDao.saveCommandRequestExecutionResult(
                        verificationInfo.verificationExecution, device.getDeviceId(), 0);
                    if (verificationInfo.isAllVerified()) {
                        infoIterator.remove();
                        log.debug("All devices verified");
                        callback.complete(StrategyType.NM);
                    }
                }
            }
        }
    }
        
    private SpecificDeviceErrorDescription getError(DeviceError error) {
        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(error.getCode());
        SpecificDeviceErrorDescription deviceErrorDescription =
            new SpecificDeviceErrorDescription(errorDescription, error.getDescriptionResolvable());

        return deviceErrorDescription;
    }

    @Override
    public StrategyType getStrategy() {
        return StrategyType.NM;
    }
}
