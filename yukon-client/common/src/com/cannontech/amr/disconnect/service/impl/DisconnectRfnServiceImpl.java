package com.cannontech.amr.disconnect.service.impl;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectRfnService;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class DisconnectRfnServiceImpl implements DisconnectRfnService {

    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private MeterDao meterDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    private Executor executor;

    private Logger log = YukonLogManager.getLogger(DisconnectRfnServiceImpl.class);

    private enum ErrorType {
        COMMUNICATION(1027),
        UNKNOWN(1026),
        YUKON_SYSTEM(2000);

        private final Integer errorCode;

        private ErrorType() {
            errorCode = null;
        }

        private ErrorType(int errorCode) {
            this.errorCode = errorCode;
        }

        public Integer getErrorCode() {
            return errorCode;
        }
    }

    @Override
    public void cancel(DisconnectResult result, YukonUserContext userContext) {
       //For RF devices, it is not possible to cancel for the devices that were already sent to NM
    }
    
    @Override
    public void execute(final DisconnectCommand command, final Iterable<SimpleDevice> meters,
                        final DisconnectCallback disconnectCallback,
                        final CommandRequestExecution execution,
                        final YukonUserContext userContext) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Iterable<YukonMeter> yukonMeters = meterDao.getMetersForYukonPaos(meters);
                int deviceCount = Lists.newArrayList(meters).size();
                PendingRequests pendingRequests = new PendingRequests(deviceCount);
                RfnMeterDisconnectStatusType type = null;
                switch (command) {
                case ARM:
                    type = RfnMeterDisconnectStatusType.ARM;
                    break;
                case CONNECT:
                    type = RfnMeterDisconnectStatusType.RESUME;
                    break;
                case DISCONNECT:
                    type = RfnMeterDisconnectStatusType.TERMINATE;
                    break;
                default:
                    throw new UnsupportedOperationException(command + " is not supported");
                }
                for (YukonMeter meter : yukonMeters) {
                    Callback callback =
                        new Callback(meter, disconnectCallback, pendingRequests, execution, userContext);
                    if (disconnectCallback.isCanceled()) {
                        callback.cancel();
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("RFN send " + type + " to "+meter);
                        }
                        rfnMeterDisconnectService.send((RfnMeter) meter, type, callback);
                    }
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

    private class Callback implements RfnMeterDisconnectCallback {
        MessageSourceAccessor messageSourceAccessor;
        private DisconnectCallback callback;
        private SimpleDevice meter;
        private PendingRequests pendingRequests;
        private CommandRequestExecution execution;

        Callback(YukonMeter meter, DisconnectCallback callback, PendingRequests pendingRequests,
                 CommandRequestExecution execution, YukonUserContext userContext) {
            this.callback = callback;
            this.meter = new SimpleDevice(meter);
            messageSourceAccessor = resolver.getMessageSourceAccessor(userContext);
            this.pendingRequests = pendingRequests;
            this.execution = execution;
        }
        
        public void cancel() {
            if (log.isDebugEnabled()) {
                log.debug("RFN send canceled:" + meter);
            }
            callback.canceled(meter);
            complete();
        }

        @Override
        public void receivedSuccess(RfnMeterDisconnectState state, PointValueQualityHolder pointData) {
            log.debug("RFN receivedSuccess");
            proccessResult(state, pointData, null);
        }

        @Override
        public void receivedError(MessageSourceResolvable message, RfnMeterDisconnectState state) {
            log.debug("RFN receivedError");
            proccessResult(state, null, message);
        }

        @Override
        public void processingExceptionOccured(MessageSourceResolvable message) {
            log.debug("RFN exception (RfnMeterDisconnectCallback)");
            SpecificDeviceErrorDescription error = getErrorDescription(ErrorType.COMMUNICATION, message);
            callback.failed(meter, error);
        }

        @Override
        public void complete() {
            if (log.isDebugEnabled()) {
                log.debug("RFN pendingRequests:" + pendingRequests.get());
            }
            int remaining = pendingRequests.decrementAndGet();
            if (remaining <= 0) {
                callback.complete();
                log.debug("RFN Complete (RfnMeterDisconnectCallback)");
            }
        }

        private SpecificDeviceErrorDescription getErrorDescription(ErrorType errorType, MessageSourceResolvable message) {
            String detail = messageSourceAccessor.getMessage(message);
            DeviceErrorDescription desc = new DeviceErrorDescription(errorType.getErrorCode(), "", "", "", detail);
            SpecificDeviceErrorDescription errorDescription = new SpecificDeviceErrorDescription(desc, detail);
            return errorDescription;
        }
        
        private void proccessResult(RfnMeterDisconnectState state, PointValueQualityHolder pointData, MessageSourceResolvable message) {
            if (log.isDebugEnabled()) {
                log.debug("RFN proccessState:" + meter + " State:" + state);
            }
            Instant timestamp = pointData == null ? null : new Instant(pointData.getPointDataTimeStamp());
            int errorCode = 0;
            switch (state) {
            case UNKNOWN:
                // devices with "UNKNOWN" state should be counted as failed
                if(message == null){
                    message = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.web.widgets.rfnMeterDisconnectWidget.sendCommand.confirmError",
                                                               state);
                }
                SpecificDeviceErrorDescription error = getErrorDescription(ErrorType.UNKNOWN, message);
                errorCode = error.getErrorCode();
                callback.failed(meter, error);
                break;
            case DISCONNECTED:
                callback.disconnected(meter, timestamp);
                break;
            case ARMED:
                callback.armed(meter, timestamp);
                break;
            case CONNECTED:
                callback.connected(meter, timestamp);
                break;
            default:
                throw new UnsupportedOperationException(state + " is not supported");
            }
            saveCommandRequestExecutionResult(execution, meter.getDeviceId(), errorCode);
        }
    }
    
    private void saveCommandRequestExecutionResult(CommandRequestExecution execution, int deviceId, int errorCode) {

        CommandRequestExecutionResult result = new CommandRequestExecutionResult();
        result.setCommandRequestExecutionId(execution.getId());
        result.setCommand(execution.getCommandRequestExecutionType().getShortName());
        result.setCompleteTime(new Date());
        result.setDeviceId(deviceId);
        result.setErrorCode(errorCode);
        commandRequestExecutionResultDao.saveOrUpdate(result);
    }

    @Resource(name="longRunningExecutor")
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
