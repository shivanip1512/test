package com.cannontech.stars.dr.hardware.builder;

import org.springframework.validation.Errors;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.YukonPao;

/**
 * This Service is designed to allow us to separate out HardwareType specific operations 
 * from the HardwareUiService.
 */
public interface HardwareTypeExtensionService {
    
    /**
     * Call to create any extra data the ExtensionService will then be responsible for.
     * @param hardware
     */
    public void createDevice(Hardware hardware);
    
    /**
     * Call to update any extra data the ExtensionService is responsible for. 
     * @param hardware
     */
    public void updateDevice(Hardware hardware);
    
    /**
     * Handles any cleanup before we call delete.
     * 
     * @param pao
     * @param inventoryId
     */
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId);
    
    /**
     * Call to delete any extra data the ExtensionService is responsible for.
     */
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId);
    
    /**
     * Call to handle any actions needed for a device when moving to inventory.
     * 
     * @param pao
     * @param inventoryId
     */
    public void moveDeviceToInventory(YukonPao pao, InventoryIdentifier inventoryId);
    
    /**
     * Fill in any extra parameters.
     * @param hardware
     */
    public void retrieveDevice(Hardware hardware);
    
    /**
     * Validate the specialized fields of the hardwareDto.
     * @param hardware
     */
    public void validateDevice(Hardware hardware, Errors errors);
    
}
