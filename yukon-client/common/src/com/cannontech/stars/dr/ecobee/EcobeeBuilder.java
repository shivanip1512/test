package com.cannontech.stars.dr.ecobee;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.CompleteDevice;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class EcobeeBuilder implements HardwareTypeExtensionProvider {
    private static final Logger log = YukonLogManager.getLogger(EcobeeBuilder.class);
        
    private static final ImmutableMap<HardwareType, PaoType> hardwareTypeToPaoType = ImmutableMap.<HardwareType, PaoType> builder()
            .put(HardwareType.ECOBEE_SMART_SI, PaoType.ECOBEE_SMART_SI)
            .put(HardwareType.ECOBEE_3, PaoType.ECOBEE_3)
            .put(HardwareType.ECOBEE_3_LITE, PaoType.ECOBEE_3_LITE)
            .put(HardwareType.ECOBEE_SMART, PaoType.ECOBEE_SMART)
            .build();
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private EcobeeZeusCommunicationService ecobeeZeusCommunicationService;
    private final Map<Integer, String> inventoryIdToSerialNumber = new HashMap<>();
    
    @Override
    public void createDevice(Hardware hardware) {
        if (isEcobeeZeusEnabled()) {
            createZeusDevice(hardware.getInventoryId(), hardware.getSerialNumber(), hardware.getHardwareType());
        } else {
            createDevice(hardware.getInventoryId(), hardware.getSerialNumber(), hardware.getHardwareType());
        }
    }
    
    public PaoIdentifier createDevice(int inventoryId, String serialNumber, HardwareType hardwareType) {
        try {
            ecobeeCommunicationService.registerDevice(serialNumber);
            
            CompleteDevice ecobeePao = new CompleteDevice();
            ecobeePao.setPaoName(serialNumber);
            paoPersistenceService.createPaoWithDefaultPoints(ecobeePao, hardwareTypeToPaoType.get(hardwareType));

            // Update the Stars table with the device id
            inventoryBaseDao.updateInventoryBaseDeviceId(inventoryId, ecobeePao.getPaObjectId());
            ecobeeCommunicationService.moveDeviceToSet(serialNumber, EcobeeCommunicationService.UNENROLLED_SET);
            return ecobeePao.getPaoIdentifier();
        } catch (Exception e) {
            //Catch any exception here - only ecobee exceptions (most often communications) are expected, but we might
            //also have authentication exceptions (which cannot be explicitly caught here) or something unexpected.
            log.error("Unable to create device.", e);
            throw new DeviceCreationException(e.getMessage(), "invalidDeviceCreation", e);
        }
    }

    /**
     * Create a Ecobee device if the provided thermostat serial number is valid and the its already enrolled.
     */
    public PaoIdentifier createZeusDevice(int inventoryId, String serialNumber, HardwareType hardwareType) {
        try {
            boolean shouldCreateDevice = ecobeeZeusCommunicationService.isDeviceRegistered(serialNumber);
            if (shouldCreateDevice) {
                CompleteDevice ecobeePao = new CompleteDevice();
                ecobeePao.setPaoName(serialNumber);
                paoPersistenceService.createPaoWithDefaultPoints(ecobeePao, hardwareTypeToPaoType.get(hardwareType));
                // Update the Stars table with the device id
                inventoryBaseDao.updateInventoryBaseDeviceId(inventoryId, ecobeePao.getPaObjectId());
                return ecobeePao.getPaoIdentifier();
            } else {
                log.error("Not creating the device as the provided thermostat serrial number is invalid or thermostat is"
                        + " not connected yet.");
                throw new DeviceCreationException("Thermostat not registered in Ecobee portal or it's yet to be connected.");
            }
        } catch (Exception e) {
            log.error("Unable to create device.", e);
            throw new DeviceCreationException(e.getMessage(), "invalidDeviceCreation", e);
        }
    }

    @Override
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId) {
        //Get the inventory, while it still exists, and cache the serial number so we can send the ecobee delete request.
        LiteInventoryBase inventory = inventoryBaseDao.getByInventoryId(inventoryId.getInventoryId());
        inventoryIdToSerialNumber.put(inventoryId.getInventoryId(), inventory.getManufacturerSerialNumber());
    }
    
    @Override
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId) {
        if (!pao.getPaoIdentifier().equals(PaoUtils.SYSTEM_PAOIDENTIFIER)) {
            paoPersistenceService.deletePao(pao.getPaoIdentifier());
        }
        // Inventory has been deleted, so get the serial number from the cache and send the ecobee delete request.
        String serialNumber = inventoryIdToSerialNumber.remove(inventoryId.getInventoryId());
        if (isEcobeeZeusEnabled()) {
            ecobeeZeusCommunicationService.deleteDevice(serialNumber);
        } else {
            ecobeeCommunicationService.deleteDevice(serialNumber);
        }
    }

    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return hardwareTypeToPaoType.keySet();
    }

    @Override
    public void updateDevice(Hardware hardware) {
        // Nothing extra to do
    }

    @Override
    public void moveDeviceToInventory(YukonPao pao, InventoryIdentifier inventoryId) {
        // Nothing extra to do
    }

    @Override
    public void retrieveDevice(Hardware hardware) {
        // Nothing extra to do
    }

    @Override
    public void validateDevice(Hardware hardware, Errors errors) {
        // Nothing extra to do
    }
    /**
     * Check Zeus is enabled through master config.
     */
    private boolean isEcobeeZeusEnabled() {
        return configurationSource.getBoolean(MasterConfigBoolean.ECOBEE_ZEUS_ENABLED);
    }
}
