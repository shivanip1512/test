package com.cannontech.amr.disconnect.service.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectPlcService;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class DisconnectPlcServiceImpl implements DisconnectPlcService{
    
    private final Logger log = YukonLogManager.getLogger(DisconnectPlcServiceImpl.class);
    private static final int MINUTES_TO_WAIT = 1;
    
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private AttributeService attributeService;
    @Autowired private StateDao stateDao;
    @Autowired @Qualifier("main") private ScheduledExecutor refreshTimer;
    
    @Override
    public void cancel(DisconnectResult result, YukonUserContext userContext) {
        if (result.getCommandCompletionCallback() != null) {
            commandRequestDeviceExecutor.cancelExecution(result.getCommandCompletionCallback(),
                                                         userContext.getYukonUser(), false);
        }
    }
            
    @Override
    public void execute(final DisconnectCommand command, Set<SimpleDevice> meters, DisconnectCallback callback,
                        CommandRequestExecution execution, DisconnectResult result, LiteYukonUser user) {

        List<CommandRequestDevice> commands =
            Lists.transform(Lists.newArrayList(meters), new Function<SimpleDevice, CommandRequestDevice>() {
                @Override
                public CommandRequestDevice apply(SimpleDevice meter) {
                    CommandRequestDevice request = new CommandRequestDevice();
                    request.setDevice(new SimpleDevice(meter));
                    request.setCommandCallback(new CommandCallbackBase(command.getPlcCommand()));
                    return request;
                }
            });
        // Copy list so we don't create new requests every time the list is traversed.
        commands = Lists.newArrayList(commands);
        if (log.isDebugEnabled()) {
            for (CommandRequestDevice c : commands) {
                log.debug("PLC send" + c);
            }
        }
        Callback commandCompletionCallback = new Callback(callback, meters);
        result.setCommandCompletionCallback(commandCompletionCallback);
        CommandRequestExecutionTemplate<CommandRequestDevice> template =
            commandRequestDeviceExecutor.getExecutionTemplate(DeviceRequestType.GROUP_CONNECT_DISCONNECT, user);
        template.execute(commands, commandCompletionCallback, execution);
    }

    private class Callback implements CommandCompletionCallback<CommandRequestDevice> {

        private final DisconnectCallback callback;
        Set<SimpleDevice> meters;
        
        Callback(DisconnectCallback callback, Set<SimpleDevice> meters) {
            this.callback = callback;
            this.meters = meters;
        }

        @Override
        public void complete() {
            log.debug("PLC Complete (CommandCompletionCallback)");
            if(log.isDebugEnabled()){
                log.debug("PLC Canceled:"+callback.isCanceled());
            }
            if (!callback.isCanceled()) {
                log.debug("PLC Completed");
                callback.complete();
            }else{
                if(log.isDebugEnabled()){
                    log.debug("PLC Waiting for "+ MINUTES_TO_WAIT + " minutes before completing");
                }
            }
        }

        @Override
        public void cancel() {
            log.debug("PLC Cancel (CommandCompletionCallback)");
            if (log.isDebugEnabled()) {
                log.debug("Wait " + MINUTES_TO_WAIT + " minutes before proccessing cancelations.");
            }
            Runnable cancelationRunner = new Runnable() {
                @Override
                public void run() {
                    if (log.isDebugEnabled()) {
                        log.debug("Proccessing cancelations for meters:" + meters);
                    }
                    for (SimpleDevice meter : meters) {
                        callback.canceled(meter);
                    }
                    log.debug("PLC Cancel Complete (CommandCompletionCallback)");
                    callback.complete();
                }
            };
            refreshTimer.schedule(cancelationRunner, MINUTES_TO_WAIT, TimeUnit.MINUTES);
        }

        @Override
        public void processingExceptionOccured(String reason) {
            log.debug("PLC exception (CommandCompletionCallback)");
            log.debug("Reason:" + reason);
            callback.processingExceptionOccured(reason);
        }

        @Override
        public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
            LitePoint litePoint =
                attributeService.getPointForAttribute(command.getDevice(), BuiltInAttribute.DISCONNECT_STATUS);
            int stateGroupId = litePoint.getStateGroupID();
            LiteState liteState = stateDao.findLiteState(stateGroupId, (int) value.getValue());
            Disconnect410State disconnectState = Disconnect410State.getByRawState(liteState.getStateRawState());
            if (disconnectState == Disconnect410State.CONFIRMED_DISCONNECTED) {
                callback.disconnected(command.getDevice(), new Instant(value.getPointDataTimeStamp()));
            } else if (disconnectState == Disconnect410State.UNCONFIRMED_DISCONNECTED) {
                callback.disconnected(command.getDevice(), new Instant(value.getPointDataTimeStamp()));
            } else if (disconnectState == Disconnect410State.CONNECTED) {
                callback.connected(command.getDevice(), new Instant(value.getPointDataTimeStamp()));
            } else if (disconnectState == Disconnect410State.CONNECT_ARMED) {
                callback.armed(command.getDevice(), new Instant(value.getPointDataTimeStamp()));
            }
            if (log.isDebugEnabled()) {
                log.debug("PLC receivedValue:" + command + " State:" + disconnectState);
            }
            meters.remove(command.getDevice());
        }
        
        @Override
        public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
            if (log.isDebugEnabled()) {
                log.debug("PLC receivedLastError:" + command + " Error:" + error);
            }
            callback.failed(command.getDevice(), error);
            meters.remove(command.getDevice());
        }
        
        @Override
        public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {}

        @Override
        public void receivedIntermediateResultString(CommandRequestDevice command, String value) {}

        @Override
        public void receivedLastResultString(CommandRequestDevice command, String value) {}
    }
    
}