package com.cannontech.amr.demandreset.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetCallback.Results;
import com.cannontech.amr.demandreset.service.RfnDemandResetService;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetReply;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetReplyType;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetRequest;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.jms.JmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RfnDemandResetServiceImpl implements RfnDemandResetService, PointDataListener {
    private static final Logger log = YukonLogManager.getLogger(RfnDemandResetServiceImpl.class);

    private final static String configurationName = "RFN_METER_DEMAND_RESET";
    private final static String queueName = "yukon.qr.obj.amr.rfn.MeterDemandResetRequest";

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PointDao pointDao;

    private ScheduledExecutorService executor = null;
    private RequestReplyTemplate<RfnMeterDemandResetReply> qrTemplate;
    private Duration verificationTimeout;

    private static class DeviceVerificationInfo {
        DemandResetCallback callback;
        Instant whenRequested;
        SimpleDevice device;

        DeviceVerificationInfo(DemandResetCallback callback, Instant whenRequested,
                                      SimpleDevice device) {
            this.callback = callback;
            this.whenRequested = whenRequested;
            this.device = device;
        }
    }
    // pointId -> DeviceAwaitingVerification
    private final Map<Integer, DeviceVerificationInfo> devicesAwaitingVerification =
            Maps.newHashMap();

    private class TimeoutChecker implements Runnable {
        @Override
        public void run() {
            log.debug("checking for timed out responses");
            synchronized (devicesAwaitingVerification) {
                Instant now = new Instant();
                for (Map.Entry<Integer, DeviceVerificationInfo> entry : devicesAwaitingVerification.entrySet()) {
                    DeviceVerificationInfo dvi = entry.getValue();
                    LogHelper.debug(log, "checking device %s; requested at %s", dvi.device,
                        dvi.whenRequested);
                    if (dvi.whenRequested.plus(verificationTimeout).isBefore(now)) {
                        LogHelper.debug(log, "timed out wating for response for %s", dvi.device);
                        Integer pointId = entry.getKey();
                        dvi.callback.cannotVerify(dvi.device,
                            "timed out waiting for point from Network Manager");
                        devicesAwaitingVerification.remove(pointId);
                        asyncDynamicDataSource.unRegisterForPointData(RfnDemandResetServiceImpl.this,
                            ImmutableSet.of(pointId));
                    }
                }
            }
        }
    }

    @PostConstruct
    public void initialize() {
        qrTemplate = new RequestReplyTemplate<RfnMeterDemandResetReply>();
        qrTemplate.setConfigurationName(configurationName);
        qrTemplate.setConfigurationSource(configurationSource);
        qrTemplate.setConnectionFactory(connectionFactory);
        qrTemplate.setRequestQueueName(queueName, false);
        verificationTimeout = configurationSource.getDuration(configurationName
            + "_VALIDATION_TIMEOUT", Duration.standardHours(26));
        log.debug("verificationTimeout = " + verificationTimeout);
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
    public void sendDemandReset(Set<? extends YukonPao> devices, final DemandResetCallback callback,
                                LiteYukonUser user) {
        LogHelper.debug(log, "sendDemandReset for %d device(s)", devices.size());
        Map<? extends YukonPao, RfnIdentifier> meterIdentifiersByPao =
                rfnDeviceDao.getRfnIdentifiersByPao(devices);
        final Map<SimpleDevice, SpecificDeviceErrorDescription> errors = Maps.newHashMap();
        final Map<RfnIdentifier, SimpleDevice> devicesByRfnMeterIdentifier = Maps.newHashMap();
        for (Map.Entry<? extends YukonPao, RfnIdentifier> entry : meterIdentifiersByPao.entrySet()) {
            YukonPao pao = entry.getKey();
            RfnIdentifier rfnMeterIdentifier = entry.getValue();
            devicesByRfnMeterIdentifier.put(rfnMeterIdentifier, new SimpleDevice(pao));
        }

        JmsReplyHandler<RfnMeterDemandResetReply> handler =
                new JmsReplyHandler<RfnMeterDemandResetReply>() {
            @Override
            public void complete() {
                callback.initiated(new Results(errors));
            }

            @Override
            public Class<RfnMeterDemandResetReply> getExpectedType() {
                return RfnMeterDemandResetReply.class;
            }

            @Override
            public void handleException(Exception e) {
                log.error("exception sending demand reset", e);
            }

            @Override
            public void handleReply(RfnMeterDemandResetReply statusReply) {
                Map<RfnIdentifier, RfnMeterDemandResetReplyType> replyTypes = statusReply.getReplyTypes();
                Collection<RfnIdentifier> rfnMeters = replyTypes.keySet();
                for (RfnIdentifier rfnMeterIdentifier : rfnMeters) {
                    RfnMeterDemandResetReplyType replyType = replyTypes.get(rfnMeterIdentifier);
                    if (replyType != RfnMeterDemandResetReplyType.OK) {
                        DeviceErrorDescription error =
                                deviceErrorTranslatorDao.translateErrorCode(replyType.getErrorCode());
                        SpecificDeviceErrorDescription deviceError =
                                new SpecificDeviceErrorDescription(error, null);
                        SimpleDevice device = devicesByRfnMeterIdentifier.get(rfnMeterIdentifier);
                        errors.put(device, deviceError);
                    }
                }
            }

            @Override
            public void handleTimeout() {
                log.error("timed out waiting for RFN demand reset");
                throw new RuntimeException("timed out waiting for RFN demand reset");
            }
        };

        List<PaoMultiPointIdentifier> paoMultipointIds =
            attributeService.findPaoMultiPointIdentifiersForNonMappedAttributes(devices,
                    ImmutableSet.of(BuiltInAttribute.RF_DEMAND_RESET));

        // Add devices to list of ones we need to send out notifications for.
        Instant now = new Instant();
        // We can't verify devices which don't have a point.
        Set<PaoIdentifier> devicesWithoutPoint =
                Sets.newHashSet(Iterables.transform(devices,
                        PaoUtils.getYukonPaoToPaoIdentifierFunction()));
        synchronized (devicesAwaitingVerification) {
            for (PaoMultiPointIdentifier paoMultiPointId : paoMultipointIds) {
                YukonPao device = paoMultiPointId.getPao();
                DeviceVerificationInfo dvi =
                    new DeviceVerificationInfo(callback, now, new SimpleDevice(device));
                // Should be a loop of one since we requested only one attribute.
                for (PaoPointIdentifier paoPointIdentifier : paoMultiPointId.getPaoPointIdentifiers()) {
                    devicesAwaitingVerification.put(pointDao.getPointId(paoPointIdentifier), dvi);
                    devicesWithoutPoint.remove(device.getPaoIdentifier());
                }
            }
            asyncDynamicDataSource.registerForPointData(this, devicesAwaitingVerification.keySet());
        }

        for (YukonPao device : devicesWithoutPoint) {
            callback.cannotVerify(new SimpleDevice(device), "\"RF Demand Reset\" point missing");
        }

        // The set returned by keySet isn't serializable, so we have to make a copy.
        Set<RfnIdentifier> meterIds = Sets.newHashSet(devicesByRfnMeterIdentifier.keySet());
        qrTemplate.send(new RfnMeterDemandResetRequest(meterIds), handler);

        setupPeriodicCheck();
    }

    private synchronized void setupPeriodicCheck() {
        // Periodically check for expired devices.
        if (executor == null) {
            LogHelper.debug(log, "inializing periodic timeout checks; verificationTimeout = %s",
                verificationTimeout);
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
            Integer pointId = pointData.getId();
            DeviceVerificationInfo dvi = devicesAwaitingVerification.get(pointId);
            if (dvi != null) {
                LogHelper.debug(log, "pointDataReceived: %s", pointData);
                Instant pointDataDate = new Instant(pointData.getPointDataTimeStamp());
                if (pointDataDate.isAfter(dvi.whenRequested)) {
                    dvi.callback.verified(dvi.device);
                } else {
                    dvi.callback.failed(dvi.device);
                }
                devicesAwaitingVerification.remove(pointId);
                asyncDynamicDataSource.unRegisterForPointData(this, ImmutableSet.of(pointId));
            }
        }
    }
}
