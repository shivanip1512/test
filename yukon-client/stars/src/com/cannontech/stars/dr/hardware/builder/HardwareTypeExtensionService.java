package com.cannontech.stars.dr.hardware.builder;

import org.springframework.validation.Errors;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.dr.hardware.model.HardwareDto;

/**
 * This Service is designed to allow us to separate out HardwareType specific operations 
 * from the HardwareUiService.
 */
public interface HardwareTypeExtensionService {
    
    /**
     * Call to create any extra data the ExtensionService will then be responsible for.
     * @param hardwareDto
     */
    public void createDevice(HardwareDto hardwareDto);
    
    /**
     * Call to update any extra data the ExtensionService is responsible for. 
     * @param hardwareDto
     */
    public void updateDevice(HardwareDto hardwareDto);
    
    /**
     * Call to delete any extra data the ExtensionService is responsible for.
     */
    public void deleteDevice(int deviceId, InventoryIdentifier id);
    
    /**
     * Fill in any extra parameters.
     * @param hardwareDto
     */
    public void retrieveDevice(HardwareDto hardwareDto);
    
    /**
     * Validate the specialized fields of the hardwareDto.
     * @param hardwareDto
     */
    public void validateDevice(HardwareDto hardwareDto, Errors errors);
    
}
