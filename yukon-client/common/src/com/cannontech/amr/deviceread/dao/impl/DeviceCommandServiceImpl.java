package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.CommandHelper;
import com.cannontech.amr.deviceread.dao.CommandHelper.ParsedCommands;
import com.cannontech.amr.deviceread.dao.CompletionCallback;
import com.cannontech.amr.deviceread.dao.DeviceCommandService;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.dao.GlobalSettingDao;

public class DeviceCommandServiceImpl implements DeviceCommandService {

    private static final Logger log = YukonLogManager.getLogger(DeviceCommandServiceImpl.class);

    @Autowired private CommandHelper commandHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private CommandRequestDeviceExecutor deviceExecutor;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationSource configurationSource;

    private ScheduledExecutorService scheduledExecutorService = null;

    private final Set<CompletionCallback> callbacksAwaitingCompletion =
        Collections.synchronizedSet(new HashSet<CompletionCallback>());

    @Override
    public CompletionCallback execute(Set<SimpleDevice> devices, Set<? extends Attribute> attributes,
            final String command, DeviceRequestType type, LiteYukonUser user, RetryParameters retryParameters,
            CommandCompletionCallbackAdapter<CommandRequestDevice> taskCallback) {

        setupTimeoutCheck();

        ParsedCommands parsedCommands = commandHelper.parseCommands(devices, attributes, command);
        Set<CommandRequestDevice> commands = parsedCommands.getCommands();
        Set<YukonPao> unsupported = parsedCommands.getUnsupportedDevices();

        CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE, type, commands.size(), user);
        commandRequestExecutionResultDao.saveUnsupported(unsupported, execution.getId(),
            CommandRequestUnsupportedType.UNSUPPORTED);

        if (log.isDebugEnabled()) {
            log.debug("Read started type= " + type + " Context id=" + execution.getContextId());
            log.debug("Context id=" + execution.getContextId() + " Unsupported devices:" + unsupported);
            log.debug("Context id=" + execution.getContextId() + " Commands:" + commands);
        }

        CompletionCallback callback =
            new CompletionCallback(deviceExecutor, commandRequestExecutionDao, commandRequestExecutionResultDao,
                globalSettingDao, configurationSource, execution, retryParameters, taskCallback, commands, user);

        if (commands.isEmpty()) {
            execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
            execution.setStopTime(new Date());
            commandRequestExecutionDao.saveOrUpdate(execution);
            if (log.isDebugEnabled()) {
                log.debug("Context id=" + execution.getContextId() + " No commands to send. Devices are unsupported.");
            }
            taskCallback.complete();
        } else {
            callbacksAwaitingCompletion.add(callback);
        }
        return callback;
    }

    private final class TimeoutChecker implements Runnable {
        @Override
        public void run() {
            synchronized (callbacksAwaitingCompletion) {
                try {
                    Iterator<CompletionCallback> iterator = callbacksAwaitingCompletion.iterator();
                    while (iterator.hasNext()) {
                        CompletionCallback callback = iterator.next();
                        callback.checkTimeout();
                        if (callback.isComplete()) {
                            iterator.remove();
                            if (log.isDebugEnabled()) {
                                log.debug(" Context id=" + callback.getContextId() + " Callback created on "
                                    + callback.getCreationTime().toDateTime().toString(
                                        CompletionCallback.dateFormatDebugPattern)
                                    + " is removed from retryCallbacksAwaitingCompletion list");
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    }

    private synchronized void setupTimeoutCheck() {
        if (scheduledExecutorService == null) {
            log.debug("Setup timeout check");
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            // runs every 5 minutes.
            scheduledExecutorService.scheduleWithFixedDelay(new TimeoutChecker(), 0, 5, TimeUnit.MINUTES);
        }
    }
}
