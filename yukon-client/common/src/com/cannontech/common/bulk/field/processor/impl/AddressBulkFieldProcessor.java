package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.service.SimpleDeviceDefinitionService;


public class AddressBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    private SimpleDeviceDefinitionService simpleDeviceService;
    
    @Override
    public void updateField(YukonDevice device, YukonDeviceDto value) throws ProcessingException  {

        try {
            simpleDeviceService.changeAddress(device, value.getAddress());
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Unable to update address.");
        }
        catch (IllegalArgumentException e) {
            throw new ProcessingException(e.getMessage());
        }
        catch (NullPointerException e) {
            
            System.out.print("asdsdfsdfsdfsdfsdfsd");
        }
    }
    
    @Required
    public void setSimpleDeviceService(SimpleDeviceDefinitionService simpleDeviceService) {
        this.simpleDeviceService = simpleDeviceService;
    }
}
