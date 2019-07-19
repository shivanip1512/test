package com.cannontech.amr.disconnect.service.impl;

import static com.cannontech.amr.disconnect.model.DisconnectCommand.ARM;
import static com.cannontech.amr.disconnect.model.DisconnectCommand.CONNECT;
import static com.cannontech.amr.disconnect.model.DisconnectCommand.DISCONNECT;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectStrategyService;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.config.RfnMeterDisconnectArming;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.DisconnectEventLogService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class DisconnectRfnServiceImpl implements DisconnectStrategyService {

    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private MeterDao meterDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired @Qualifier("longRunning") private Executor executor;
    @Autowired private DisconnectEventLogService disconnectEventLogService;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private ConfigurationSource configurationSource;
        
    private static final Logger log = YukonLogManager.getLogger(DisconnectRfnServiceImpl.class);
    private static final Logger rfnLogger = YukonLogManager.getRfnLogger();
    
    private RfnMeterDisconnectArming mode;
    private Set<PaoType> validTypes;

    @PostConstruct
    public void init() {
        mode = RfnMeterDisconnectArming.getForCparm(
            configurationSource.getString(MasterConfigString.RFN_METER_DISCONNECT_ARMING, "FALSE"));
        validTypes = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_RFN).stream()
                .map(d -> d.getType())
                .collect(Collectors.toSet());
    }
    
    @Override
    public FilteredDevices filter(List<SimpleDevice> meters) {
        FilteredDevices filteredDevices = new FilteredDevices();
        filteredDevices.addValid(meters.stream()
            .filter(meter -> validTypes.contains(meter.getDeviceType()))
            .collect(Collectors.toList()));
        return filteredDevices;
    }

    @Override
    public boolean supportsArm(List<SimpleDevice> meters) {
        return (mode == RfnMeterDisconnectArming.ARM || mode == RfnMeterDisconnectArming.BOTH)
            && meters.stream()
            .filter(meter -> meter.getDeviceType().isRfMeter())
            .findFirst().isPresent();
    }
    
    @Override
    public void execute(DisconnectCommand command, Set<SimpleDevice> meters, DisconnectCallback disconnectCallback,
            CommandRequestExecution execution, LiteYukonUser user) {
        
        if(disconnectCallback.getResult() != null) {
            disconnectCallback.getResult().addCancellationCallback(
                new CollectionActionCancellationCallback(getStrategy(), disconnectCallback));
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Iterable<YukonMeter> yukonMeters = meterDao.getMetersForYukonPaos(meters);
                PendingRequests pendingRequests = new PendingRequests(meters.size());
                for (YukonMeter meter : yukonMeters) {
                    if (disconnectCallback.getResult() != null && disconnectCallback.getResult().isCanceled()) {
                        break;
                    }
                    Callback callback = new Callback(meter, disconnectCallback, pendingRequests, execution);

                    if (rfnLogger.isDebugEnabled()) {
                        rfnLogger.debug("<<< Sent disconnect command: " + command.getRfnMeterDisconnectStatusType()
                            + " to " + ((RfnMeter) meter).getRfnIdentifier());
                    }
                    rfnMeterDisconnectService.send((RfnMeter) meter, command.getRfnMeterDisconnectStatusType(),
                        callback);
                }
            }
        };
        executor.execute(runnable);
    }

    private class PendingRequests {
        private AtomicInteger pendingRequests;

        PendingRequests(int deviceCount) {
            pendingRequests = new AtomicInteger(deviceCount);
        }

        int decrementAndGet() {
            return pendingRequests.decrementAndGet();
        }

        int get() {
            return pendingRequests.get();
        }
    }

    public class Callback implements RfnMeterDisconnectCallback {
        private DisconnectCallback callback;
        private SimpleDevice meter;
        private PendingRequests pendingRequests;
        private CommandRequestExecution execution;

        Callback(YukonMeter meter, DisconnectCallback callback, PendingRequests pendingRequests,
                CommandRequestExecution execution) {
            this.callback = callback;
            this.meter = new SimpleDevice(meter);
            this.pendingRequests = pendingRequests;
            this.execution = execution;
        }

        @Override
        public void receivedSuccess(RfnDisconnectStatusState state, PointValueQualityHolder pointData) {
            log.debug("RFN receivedSuccess");
            proccessResult(state, pointData, null, null);
        }

        @Override
        public void receivedError(MessageSourceResolvable message, RfnDisconnectStatusState state,
                RfnMeterDisconnectConfirmationReplyType replyType) {
            log.debug("RFN receivedError");
            proccessResult(state, null, message, replyType);

        }

        @Override
        public void processingExceptionOccurred(MessageSourceResolvable message) {
            log.debug("RFN exception (RfnMeterDisconnectCallback)");
            DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.FAILURE);
            SpecificDeviceErrorDescription error = new SpecificDeviceErrorDescription(errorDescription, message);

            callback.failed(meter, error);
        }

        @Override
        public void complete() {
            if (log.isDebugEnabled()) {
                log.debug("RFN pendingRequests:" + pendingRequests.get());
            }
            int remaining = pendingRequests.decrementAndGet();
            if (remaining <= 0) {
                callback.complete(getStrategy());
                log.debug("RFN Complete (RfnMeterDisconnectCallback)");
            }
        }

        private void proccessResult(RfnDisconnectStatusState state, PointValueQualityHolder pointData,
                MessageSourceResolvable message, RfnMeterDisconnectConfirmationReplyType replyType) {
            if (rfnLogger.isInfoEnabled()) {
                rfnLogger.info("RFN proccessState:" + meter + " state:" + state);
            }
            /*
             * message is null if the state == UNKNOWN
             * state is null if there was an error sending the command
             */
            if (message == null) {
                message = YukonMessageSourceResolvable.createSingleCodeWithArguments(
                    "yukon.web.widgets.disconnectMeterWidget.rfn.sendCommand.confirmError", state);
            }

            int errorCode = 0;
            DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.FAILURE);
            SpecificDeviceErrorDescription error = new SpecificDeviceErrorDescription(errorDescription, message);

            if (replyType == RfnMeterDisconnectConfirmationReplyType.FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT) {
                disconnectEventLogService.loadSideVoltageDetectedWhileDisconnected(
                    YukonUserContext.system.getYukonUser(),
                    databaseCache.getAllPaosMap().get(meter.getDeviceId()).getPaoName());
                error = new SpecificDeviceErrorDescription(
                    deviceErrorTranslatorDao.translateErrorCode(
                        DeviceError.FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT),
                    YukonMessageSourceResolvable.createSingleCodeWithArguments(
                        "yukon.web.widgets.disconnectMeterWidget.error.loadSideVoltageDetectedWhileDisconnected"));
                callback.failed(meter, error);
                errorCode = error.getErrorCode();
            } else if (replyType == RfnMeterDisconnectConfirmationReplyType.FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD) {
                error = new SpecificDeviceErrorDescription(
                    deviceErrorTranslatorDao.translateErrorCode(
                        DeviceError.FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD),
                    YukonMessageSourceResolvable.createSingleCodeWithArguments(
                        "yukon.web.widgets.disconnectMeterWidget.error.loadSideVoltageHigherThanThreshold"));
                callback.failed(meter, error);
                errorCode = error.getErrorCode();
            } else if (replyType == RfnMeterDisconnectConfirmationReplyType.FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT) {
                error = new SpecificDeviceErrorDescription(
                    deviceErrorTranslatorDao.translateErrorCode(
                        DeviceError.FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT),
                    YukonMessageSourceResolvable.createSingleCodeWithArguments(
                        "yukon.web.widgets.disconnectMeterWidget.error.noLoadSideVoltageDetectedWhileConnected"));
                callback.failed(meter, error);
                errorCode = error.getErrorCode();
            } else if (state == null) {
                callback.failed(meter, error);
                errorCode = error.getErrorCode();
            } else {
                switch (state) {
                case UNKNOWN:
                    // devices with "UNKNOWN" state should be counted as failed
                    callback.failed(meter, error);
                    errorCode = error.getErrorCode();
                    break;
                case DISCONNECTED:
                case DISCONNECTED_DEMAND_THRESHOLD_ACTIVE:
                case CONNECTED_DEMAND_THRESHOLD_ACTIVE:
                case DISCONNECTED_CYCLING_ACTIVE:
                case CONNECTED_CYCLING_ACTIVE:
                    callback.success(DISCONNECT, meter, pointData);
                    break;
                case ARMED:
                    callback.success(ARM, meter, pointData);
                    break;
                case CONNECTED:
                    callback.success(CONNECT, meter, pointData);
                    break;
                default:
                    throw new UnsupportedOperationException(state + " is not supported");
                }
            }
            commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution, meter.getDeviceId(),
                errorCode);
        }
    }

    @Override
    public StrategyType getStrategy() {
        return StrategyType.NM;
    }
    
    @Override
    public void cancel(CollectionActionResult result, LiteYukonUser user) {
        // doesn't support cancellation
        result.getCancellationCallbacks(getStrategy()).forEach(callback -> {
            callback.cancel();
        });
    }
}