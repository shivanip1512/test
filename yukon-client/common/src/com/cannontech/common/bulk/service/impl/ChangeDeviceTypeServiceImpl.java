package com.cannontech.common.bulk.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService;
import com.cannontech.common.device.DeviceType;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class ChangeDeviceTypeServiceImpl implements ChangeDeviceTypeService {

    private DeviceDefinitionDao deviceDefinitionDao;
    private PaoDao paoDao;
    private DeviceDefinitionService deviceDefinitionService;
    private DeviceUpdateService deviceUpdateService;
    
    public SimpleDevice changeDeviceType(SimpleDevice device, DeviceType newDeviceType ) {

        try {

            // get the definition for the type selected
            if (newDeviceType == device.getDeviceType()) {
                return device;
            }

            DeviceDefinition selectedDeviceDefinition = deviceDefinitionDao.getDeviceDefinition(newDeviceType);

            // get set of all definition applicable for this device to be changed to
            Set<DeviceDefinition> applicableDefinitions = deviceDefinitionService.getChangeableDevices(device);

            // if its not in the set, throw a processing error
            if (!applicableDefinitions.contains(selectedDeviceDefinition)) {

                LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
                String errorMsg = selectedDeviceDefinition.getDisplayName() + " is not an applicable type for device: " + devicePao.getPaoName();
                throw new ProcessingException(errorMsg);
            }
            else {
                return deviceUpdateService.changeDeviceType(device, selectedDeviceDefinition);
            }

        }
        catch (IllegalArgumentException e) {
            throw new ProcessingException("Invalid device type: " + newDeviceType);
        } catch (DataRetrievalFailureException e) {
            throw new ProcessingException("Could not find device with id: " + device.getDeviceId(),
                                          e);
        } catch (PersistenceException e) {
            throw new ProcessingException("Could not change device type for device with id: " + device.getDeviceId(), e);
        }
    }
    
    @Autowired
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setDeviceDefinitionService(
            DeviceDefinitionService deviceDefinitionService) {
        this.deviceDefinitionService = deviceDefinitionService;
    }
    
    @Autowired
    public void setDeviceUpdateService(DeviceUpdateService deviceUpdateService) {
		this.deviceUpdateService = deviceUpdateService;
	}

}
