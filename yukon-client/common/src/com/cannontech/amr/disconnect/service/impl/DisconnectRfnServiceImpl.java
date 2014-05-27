package com.cannontech.amr.disconnect.service.impl;

import java.util.Date;
import java.util.Set;
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
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.service.NetworkManagerError;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class DisconnectRfnServiceImpl implements DisconnectRfnService {

    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private MeterDao meterDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    private Executor executor;
    private final Logger log = YukonLogManager.getLogger(DisconnectRfnServiceImpl.class);
    
    @Override
    public void cancel(DisconnectResult result, YukonUserContext userContext) {
       //For RF devices, it is not possible to cancel for the devices that were already sent to NM
    }
    
    @Override
    public void execute(final DisconnectCommand command, final Set<SimpleDevice> meters,
                        final DisconnectCallback disconnectCallback,
                        final CommandRequestExecution execution,
                        final YukonUserContext userContext) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Iterable<YukonMeter> yukonMeters = meterDao.getMetersForYukonPaos(meters);
                PendingRequests pendingRequests = new PendingRequests(meters.size());
                for (YukonMeter meter : yukonMeters) {
                    Callback callback =
                        new Callback(meter, disconnectCallback, pendingRequests, execution, userContext);
                    if (disconnectCallback.isCanceled()) {
                        callback.cancel();
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("RFN send " + command.getRfnMeterDisconnectStatusType() + " to "+meter);
                        }
                        rfnMeterDisconnectService.send((RfnMeter) meter, command.getRfnMeterDisconnectStatusType(), callback);
                    }
                }
            }
        };
        executor.execute(runnable);
    }
    
    private class PendingRequests {
        private final AtomicInteger pendingRequests;

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
        private final DisconnectCallback callback;
        private final SimpleDevice meter;
        private final PendingRequests pendingRequests;
        private final CommandRequestExecution execution;

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
            SpecificDeviceErrorDescription error = getErrorDescription(NetworkManagerError.FAILURE, message);
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

        private SpecificDeviceErrorDescription getErrorDescription(NetworkManagerError errorType, MessageSourceResolvable message) {
            String detail = messageSourceAccessor.getMessage(message);
            DeviceErrorDescription desc = new DeviceErrorDescription(errorType.getErrorCode(), "", "",  detail, "");
            SpecificDeviceErrorDescription errorDescription = new SpecificDeviceErrorDescription(desc, detail);
            return errorDescription;
        }
        
        private void proccessResult(RfnMeterDisconnectState state, PointValueQualityHolder pointData, MessageSourceResolvable message) {
            if (log.isDebugEnabled()) {
                log.debug("RFN proccessState:" + meter + " State:" + state);
            }
            /*
             * message is null if the state == UNKNOWN
             * state is null if there was an error sending the command
             */
            
            if (message == null) {
                message =
                    YukonMessageSourceResolvable
                        .createSingleCodeWithArguments("yukon.web.widgets.rfnMeterDisconnectWidget.sendCommand.confirmError",
                                                       state);
            }
            SpecificDeviceErrorDescription error = getErrorDescription(NetworkManagerError.FAILURE, message);
            int errorCode = 0;
            if (state == null) {
                callback.failed(meter, error);
                errorCode = error.getErrorCode();
            } else {
                Instant timestamp = pointData == null ? null : new Instant(pointData.getPointDataTimeStamp());
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
