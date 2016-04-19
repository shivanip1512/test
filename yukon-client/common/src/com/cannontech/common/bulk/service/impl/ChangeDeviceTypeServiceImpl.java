package com.cannontech.common.bulk.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class ChangeDeviceTypeServiceImpl implements ChangeDeviceTypeService {

    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionService paoDefinitionService;
    @Autowired private DeviceUpdateService deviceUpdateService;
      
    @Override
    public SimpleDevice changeDeviceType(SimpleDevice device, PaoType newDeviceType, ChangeDeviceTypeInfo info) {

        try {
                        
            // get the definition for the type selected
            if (newDeviceType == device.getDeviceType()) {
                return device;
            }

            PaoDefinition selectedPaoDefinition = paoDefinitionDao.getPaoDefinition(newDeviceType);

            // get set of all definition applicable for this device to be changed to
            Set<PaoDefinition> applicableDefinitions = paoDefinitionService.getChangeablePaos(device);

            // if its not in the set, throw a processing error
            if (!applicableDefinitions.contains(selectedPaoDefinition)) {

                LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
                String errorMsg = selectedPaoDefinition.getDisplayName() + " is not an compatible type for device: " + devicePao.getPaoName() + "; Type:" + devicePao.getPaoType();
                
                throw new ProcessingException(errorMsg,
                                              "deviceIncompatible",
                                              selectedPaoDefinition.getDisplayName(),
                                              devicePao.getPaoName(),
                                              devicePao.getPaoType());
            }
            else {
                return deviceUpdateService.changeDeviceType(device, selectedPaoDefinition, info);
            }
        }
        catch (IllegalArgumentException e) {
            throw new ProcessingException("Invalid device type: " + newDeviceType, "invalidDeviceType", e, newDeviceType );
        } catch (DataRetrievalFailureException e) {
            throw new ProcessingException("Could not find device with id: " + device.getDeviceId(), "deviceNotFoundWithId",
                                          e, device.getDeviceId());
        } catch (PersistenceException e) {
            throw new ProcessingException("Could not change device type for device with id: " + device.getDeviceId(),
                                          "changeDeviceType",
                                          e,
                                          device.getDeviceId());
        }
    }
}
