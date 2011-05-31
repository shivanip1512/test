package com.cannontech.amr.deviceread.dao.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.deviceread.service.GroupMeterReadService;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.impl.SpecificDeviceErrorDescription;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class DeviceAttributeReadPlcStrategy implements DeviceAttributeReadStrategy {
    private PaoDefinitionDao paoDefinitionDao;
    private GroupMeterReadService groupMeterReadService;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper; 
    private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    
    @Override
    public DeviceAttributeReadStrategyType getType() {
        return DeviceAttributeReadStrategyType.PLC;
    }
    
    @Override
    public boolean canRead(PaoType paoType) {
        boolean result = paoDefinitionDao.isTagSupported(paoType, PaoTag.PORTER_COMMAND_REQUESTS);
        return result;
    }
    
    @Override
    public boolean isReadable(Iterable<? extends YukonPao> devices, Set<Attribute> attributes,
            LiteYukonUser user) {
        for (YukonPao device : devices) {
            // I feel a little bad about going around the groupMeterReadService's back, but this will have to do.
            boolean readable = meterReadCommandGeneratorService.isReadable(PaoUtils.asYukonDevice(device), attributes);
            if (readable) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initiateRead(Iterable<? extends YukonPao> devices,
            Set<? extends Attribute> attributes, final DeviceAttributeReadCallback delegateCallback,
            DeviceRequestType type, LiteYukonUser user) {
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.createDeviceGroupCollection(PaoUtils.asSimpleDeviceListFromPaos(devices).iterator(), null);
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
            public void processingExceptionOccured(String reason) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.plc.exception", reason);
                DeviceAttributeReadError exception = new DeviceAttributeReadError(DeviceAttributeReadErrorType.EXCEPTION, summary);
                delegateCallback.receivedException(exception);
            }
        };
        groupMeterReadService.backgroundReadDeviceCollection(deviceCollection, attributes, type, groupCallback, user, RetryParameters.none());
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Autowired
    public void setMeterReadCommandGeneratorService(
            MeterReadCommandGeneratorService meterReadCommandGeneratorService) {
        this.meterReadCommandGeneratorService = meterReadCommandGeneratorService;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(
            DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    @Autowired
    public void setGroupMeterReadService(GroupMeterReadService groupMeterReadService) {
        this.groupMeterReadService = groupMeterReadService;
    }

}
