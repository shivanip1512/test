package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class DeviceAttributeReadPlcStrategy implements DeviceAttributeReadStrategy {

    private final static Logger log = YukonLogManager.getLogger(DeviceAttributeReadPlcStrategy.class);
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private CommandExecutionService commandRequestService;
    
    @Override
    public StrategyType getStrategy() {
        return StrategyType.PLC;
    }

    @Override
    public boolean canRead(PaoType paoType) {
        return paoType.isPlc() && paoDefinitionDao.isTagSupported(paoType, PaoTag.PORTER_COMMAND_REQUESTS);
    }

    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> paoPointIdentifiers) {
        return meterReadCommandGeneratorService.isReadable(paoPointIdentifiers);
    }

    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> points, DeviceAttributeReadCallback callback,
            CommandRequestExecution execution, LiteYukonUser user) {
        List<CommandRequestDevice> commands = meterReadCommandGeneratorService.getCommandRequests(points);
        if (log.isDebugEnabled()) {
            log.debug(getStrategy() + " Strategy initiateRead");
            log.debug("Points:" + points);
            log.debug("Commands:" + commands);
        }
        commandRequestService.execute(commands, getCompletionCallback(callback, commands), execution, false, user);
    }
        
    private CommandCompletionCallback<CommandRequestDevice> getCompletionCallback(
            DeviceAttributeReadCallback callback, List<CommandRequestDevice> commands) {
        CommandCompletionCallback<CommandRequestDevice> creCallback =
            new CommandCompletionCallback<CommandRequestDevice>() {
                List<PaoIdentifier> allDevices = Collections.synchronizedList(new ArrayList<PaoIdentifier>());
                {
                    if(callback.getResult() != null) {
                       allDevices.addAll(commands.stream()
                           .map(command -> command.getDevice().getPaoIdentifier())
                           .collect(Collectors.toSet()));
                    }
                }
                @Override
                public void complete() {
                    callback.complete(getStrategy());
                }

                @Override
                public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    allDevices.remove(command.getDevice().getPaoIdentifier());
                    callback.receivedError(command.getDevice().getPaoIdentifier(), error);
                }

                @Override
                public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
                    callback.receivedValue(command.getDevice().getPaoIdentifier(), value);
                }

                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    allDevices.remove(command.getDevice().getPaoIdentifier());
                    callback.receivedLastValue(command.getDevice().getPaoIdentifier(), value);
                }

                @Override
                public void processingExceptionOccured(String reason) {
                    DeviceErrorDescription errorDescription =
                        deviceErrorTranslatorDao.translateErrorCode(DeviceError.TIMEOUT);
                    MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments(
                        "yukon.common.device.attributeRead.plc.exception", reason);
                    MessageSourceResolvable detail = YukonMessageSourceResolvable.createSingleCode(
                        "yukon.common.device.attributeRead.plc.unknownError");
                    SpecificDeviceErrorDescription error =
                        new SpecificDeviceErrorDescription(errorDescription, summary, detail);
                    callback.receivedException(error);
                }
                
                @Override
                public void cancel() {
                   callback.cancel(allDevices);
                   callback.complete();
                  
                }

            };
            if(callback.getResult() != null) {
                callback.getResult().setCancelationCallback(creCallback);
            }
            return creCallback;
    }

    @Override
    public int getRequestCount(Collection<PaoMultiPointIdentifier> devicesForThisStrategy) {
        List<CommandRequestDevice> commandRequests =
            meterReadCommandGeneratorService.getCommandRequests(devicesForThisStrategy);
        return commandRequests.size();
    }

    
    @Override
    public void cancel(CollectionActionResult result, LiteYukonUser user) {
        if (result.getCancelationCallback() != null) {
            commandRequestService.cancelExecution(result.getCancelationCallback(), user, false);
        }
    }
}