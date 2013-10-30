package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;

public class DeviceAttributeReadPlcStrategy implements DeviceAttributeReadStrategy {
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PlcDeviceAttributeReadService plcDeviceAttributeReadService;
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    
    @Override
    public StrategyType getType() {
        return StrategyType.PLC;
    }
    
    @Override
    public boolean canRead(PaoType paoType) {
        boolean result = false;
        if(paoType.isPlc()){
            result = paoDefinitionDao.isTagSupported(paoType, PaoTag.PORTER_COMMAND_REQUESTS);
        }
        return result;
    }
    
    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> points, LiteYukonUser user) {
        return plcDeviceAttributeReadService.isReadable(points, user);
    }

    @Override
    public void initiateRead(final Iterable<PaoMultiPointIdentifier> points,
            GroupCommandCompletionCallback groupCallback,
            DeviceRequestType type, YukonUserContext userContext) {
        CommandRequestExecutionTemplate<CommandRequestDevice> template =
                commandRequestDeviceExecutor.getExecutionTemplate(type, userContext.getYukonUser());
        List<CommandRequestDevice> commands = meterReadCommandGeneratorService.getCommandRequests(points);
        template.execute(commands, groupCallback, groupCallback.getExecution());
    }

       
    @Override
    public void initiateRead(final Iterable<PaoMultiPointIdentifier> points,
            final DeviceAttributeReadStrategyCallback delegateCallback,
            DeviceRequestType type, LiteYukonUser user) {
        CommandCompletionCallback<CommandRequestDevice> groupCallback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {

            @Override
            public void complete() {
                delegateCallback.complete();
            }

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.plc.errorSummary", error.getCategory(), error.getDescription(), error.getErrorCode(), error.getPorter());
                MessageSourceResolvable detail = YukonMessageSourceResolvable.createDefaultWithoutCode(error.getTroubleshooting());
                DeviceAttributeReadError readError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.COMMUNICATION, summary, detail);
                delegateCallback.receivedError(command.getDevice().getPaoIdentifier(), readError);
            }
            
            @Override
            public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
                delegateCallback.receivedValue(command.getDevice().getPaoIdentifier(), value);
            }
            
            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                delegateCallback.receivedLastValue(command.getDevice().getPaoIdentifier());
            }
            
            @Override
            public void processingExceptionOccured(String reason) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.plc.exception", reason);
                DeviceAttributeReadError exception = new DeviceAttributeReadError(DeviceAttributeReadErrorType.EXCEPTION, summary);
                delegateCallback.receivedException(exception);
            }
        };
        plcDeviceAttributeReadService.backgroundReadDeviceCollection(points, type, groupCallback, user, RetryParameters.none());
    }
    

    @Override
    public int getRequestCount(Collection<PaoMultiPointIdentifier> devicesForThisStrategy) {
        List<CommandRequestDevice> commandRequests =
                meterReadCommandGeneratorService.getCommandRequests(devicesForThisStrategy);
        return commandRequests.size();
    }
}
