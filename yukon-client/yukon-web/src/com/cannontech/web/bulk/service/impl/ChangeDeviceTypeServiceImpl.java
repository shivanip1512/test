package com.cannontech.web.bulk.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.web.bulk.service.ChangeDeviceTypeService;

public class ChangeDeviceTypeServiceImpl implements ChangeDeviceTypeService {

    private DeviceDefinitionDao deviceDefinitionDao;
    private PaoGroupsWrapper paoGroupsWrapper;
    private PaoDao paoDao;
    private DeviceDefinitionService deviceDefinitionService;
    
    public YukonDevice processDeviceTypeChange(YukonDevice device, int newDeviceType ) {

        try {

            // get the definition for the type selected
            if (newDeviceType == device.getType()) {
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
                return deviceDefinitionService.changeDeviceType(device, selectedDeviceDefinition);
            }

        }
        catch (IllegalArgumentException e) {
            throw new ProcessingException("Invalid device type: " + paoGroupsWrapper.getPAOTypeString(newDeviceType));
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
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
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

}
