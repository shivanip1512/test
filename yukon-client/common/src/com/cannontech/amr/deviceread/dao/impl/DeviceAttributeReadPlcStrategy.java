package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionObjects;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.impl.CommandRequestRetryExecutor;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifierWithUnsupported;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class DeviceAttributeReadPlcStrategy implements DeviceAttributeReadStrategy {
    
    private final static Logger log = YukonLogManager.getLogger(DeviceAttributeReadPlcStrategy.class);
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
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
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> paoPointIdentifiers, LiteYukonUser user) {
        return meterReadCommandGeneratorService.isReadable(paoPointIdentifiers);
    }
   
    @Override
    public void initiateRead(final Iterable<PaoMultiPointIdentifier> points,
            GroupCommandCompletionCallback groupCallback,
            DeviceRequestType type, LiteYukonUser user) {    
        List<CommandRequestDevice> commands = meterReadCommandGeneratorService.getCommandRequests(points);
        if (log.isDebugEnabled()) {
            log.debug(getType() + " Strategy initiateRead");
            log.debug("Points:" + points);
            log.debug("Commands:" + commands);
        }
		commandRequestDeviceExecutor.createTemplateAndExecute(groupCallback.getExecution(), groupCallback, commands, user, true);
    }
 
    @Override
    public void initiateRead(final Iterable<PaoMultiPointIdentifier> points,
            final DeviceAttributeReadStrategyCallback delegateCallback,
            DeviceRequestType type, CommandRequestExecution execution, LiteYukonUser user) {
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
                delegateCallback.receivedLastValue(command.getDevice().getPaoIdentifier(), value);
            }
            
            @Override
            public void processingExceptionOccured(String reason) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.plc.exception", reason);
                MessageSourceResolvable detail = YukonMessageSourceResolvable.createSingleCode("yukon.common.device.attributeRead.plc.unknownError");
                DeviceAttributeReadError exception = new DeviceAttributeReadError(DeviceAttributeReadErrorType.EXCEPTION, summary, detail);
                delegateCallback.receivedException(exception);
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
    
    @Override
    public CommandRequestExecutionObjects<CommandRequestDevice> initiateRead(DeviceCollection deviceCollection,
                                                                                        Set<? extends Attribute> attributes,
                                                                                        DeviceRequestType type,
                                                                                        CommandCompletionCallback<CommandRequestDevice> callback,
                                                                                        LiteYukonUser user,
                                                                                        RetryParameters retryParameters) {

        CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE, type, 0, user);

        PaoMultiPointIdentifierWithUnsupported paoPointIdentifiers =
            attributeService.findPaoMultiPointIdentifiersForAttributes(deviceCollection, attributes);

        Set<YukonPao> unsupportedDevices = new HashSet<>();

        // MCTs that support the attributes
        List<PaoMultiPointIdentifier> supportedDevices = new ArrayList<>();

        unsupportedDevices.addAll(paoPointIdentifiers.getUnsupportedDevices().keySet());

   
        for (PaoMultiPointIdentifier identifier : paoPointIdentifiers.getDevicesAndPoints()) {
            if (identifier.getPao().getPaoType().isMct()) {
                supportedDevices.add(identifier);
            } else {
            	//Adding devices that support the attribute and that are not MCTs. 
                unsupportedDevices.add(identifier.getPao());
            }
        }
        
        log.debug("Unsupported devices:"+ unsupportedDevices);
        
        commandRequestExecutionResultDao.saveUnsupported(unsupportedDevices,
                                                         execution.getId(),
                                                         CommandRequestUnsupportedType.UNSUPPORTED);

        CommandRequestExecutionObjects<CommandRequestDevice> executionObjects = null;
        if (!supportedDevices.isEmpty()) {
            List<CommandRequestDevice> commandRequests =
                meterReadCommandGeneratorService.getCommandRequests(supportedDevices);
            execution.setRequestCount(commandRequests.size());
            commandRequestExecutionDao.saveOrUpdate(execution);
            CommandRequestRetryExecutor<CommandRequestDevice> retryExecutor =
                new CommandRequestRetryExecutor<>(commandRequestDeviceExecutor, retryParameters);
            executionObjects = retryExecutor.execute(commandRequests, callback, execution, user);
        } else {
            execution.setStopTime(new Date());
            execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
            commandRequestExecutionDao.saveOrUpdate(execution);
            CommandRequestExecutionContextId commandRequestExecutionContextId =
                new CommandRequestExecutionContextId(execution.getContextId());
			executionObjects = new CommandRequestExecutionObjects<CommandRequestDevice>(null, null, commandRequestExecutionContextId);
			callback.complete();
        }

        return executionObjects;
    }
}
