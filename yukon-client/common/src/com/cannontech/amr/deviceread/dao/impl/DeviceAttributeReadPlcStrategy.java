package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
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
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    @Override
    public StrategyType getType() {
        return StrategyType.PLC;
    }

    @Override
    public boolean canRead(PaoType paoType) {
        boolean result = false;
        if (paoType.isPlc()) {
            result = paoDefinitionDao.isTagSupported(paoType, PaoTag.PORTER_COMMAND_REQUESTS);
        }
        return result;
    }

    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> paoPointIdentifiers, LiteYukonUser user) {
        return meterReadCommandGeneratorService.isReadable(paoPointIdentifiers);
    }

    @Override
    public void initiateRead(final Iterable<PaoMultiPointIdentifier> points,
            GroupCommandCompletionCallback groupCallback, DeviceRequestType type, LiteYukonUser user) {
        List<CommandRequestDevice> commands = meterReadCommandGeneratorService.getCommandRequests(points);
        if (log.isDebugEnabled()) {
            log.debug(getType() + " Strategy initiateRead");
            log.debug("Points:" + points);
            log.debug("Commands:" + commands);
        }
        commandRequestDeviceExecutor.createTemplateAndExecute(groupCallback.getExecution(), groupCallback, commands,
            user, true);
    }

    @Override
    public void initiateRead(final Iterable<PaoMultiPointIdentifier> points,
            final DeviceAttributeReadStrategyCallback delegateCallback, DeviceRequestType type,
            CommandRequestExecution execution, LiteYukonUser user) {
        CommandCompletionCallback<CommandRequestDevice> groupCallback =
            new CommandCompletionCallbackAdapter<CommandRequestDevice>() {

                @Override
                public void complete() {
                    delegateCallback.complete();
                }

                @Override
                public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    delegateCallback.receivedError(command.getDevice().getPaoIdentifier(), error);
                }

                @Override
                public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
                    delegateCallback.receivedValue(command.getDevice().getPaoIdentifier(), value);
                }

                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    delegateCallback.receivedLastValue(command.getDevice().getPaoIdentifier(), value);
                }

                @Override
                public void processingExceptionOccured(String reason) {
                    DeviceErrorDescription errorDescription =
                        deviceErrorTranslatorDao.translateErrorCode(DeviceError.TIMEOUT);
                    MessageSourceResolvable summary =
                        YukonMessageSourceResolvable.createSingleCodeWithArguments(
                            "yukon.common.device.attributeRead.plc.exception", reason);
                    MessageSourceResolvable detail =
                        YukonMessageSourceResolvable.createSingleCode("yukon.common.device.attributeRead.plc.unknownError");
                    SpecificDeviceErrorDescription error =
                        new SpecificDeviceErrorDescription(errorDescription, summary, detail);
                    delegateCallback.receivedException(error);
                }
            };

        List<CommandRequestDevice> commands = meterReadCommandGeneratorService.getCommandRequests(points);
        if (log.isDebugEnabled()) {
            log.debug(getType() + " Strategy initiateRead");
            log.debug("Points:" + points);
            log.debug("Commands:" + commands);
        }
        commandRequestDeviceExecutor.createTemplateAndExecute(execution, groupCallback, commands, user, true);
    }

    @Override
    public int getRequestCount(Collection<PaoMultiPointIdentifier> devicesForThisStrategy) {
        List<CommandRequestDevice> commandRequests =
            meterReadCommandGeneratorService.getCommandRequests(devicesForThisStrategy);
        return commandRequests.size();
    }
}