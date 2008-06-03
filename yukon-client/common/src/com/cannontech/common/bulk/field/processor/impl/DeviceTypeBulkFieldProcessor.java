package com.cannontech.common.bulk.field.processor.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class DeviceTypeBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    private PaoDao paoDao = null;
    private DeviceDefinitionService deviceDefinitionService = null;
    private DeviceDefinitionDao deviceDefinitionDao = null;
    
    @Override
    public void updateField(YukonDevice device, YukonDeviceDto value) {
        
        try {
            
            // get the definition for the type selected
            DeviceDefinition selectedDeviceDefinition = deviceDefinitionDao.getDeviceDefinition(value.getDeviceType());
            
            // get set of all definition applicable for this device to be changed to
            Set<DeviceDefinition> applicableDefinitions = deviceDefinitionService.getChangeableDevices(device);
            
            // if its not in the set, throw a processing error
            if (!applicableDefinitions.contains(selectedDeviceDefinition)) {
                
                LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
                String errorMsg = selectedDeviceDefinition.getDisplayName() + " is not an applicable type for device: " + devicePao.getPaoName();
                throw new ProcessingException(errorMsg);
            }
            else {
                deviceDefinitionService.changeDeviceType(device, selectedDeviceDefinition);
            }
        
        } catch (DataRetrievalFailureException e) {
            throw new ProcessingException("Could not find device with id: " + device.getDeviceId(),
                                          e);
        } catch (PersistenceException e) {
            throw new ProcessingException("Could not change device type for device with id: " + device.getDeviceId(), e);
        }
    }

    @Required
    public void setDeviceDefinitionService(DeviceDefinitionService deviceDefinitionService) {
        this.deviceDefinitionService = deviceDefinitionService;
    }
    
    @Required
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
