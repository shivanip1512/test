package com.cannontech.stars.dr.honeywell;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.CompleteHoneywellWifiThermostat;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.cannontech.util.Validator;

public class HoneywellBuilder implements HardwareTypeExtensionProvider {
    private static final Logger log = YukonLogManager.getLogger(HoneywellBuilder.class);
        
    private static final ImmutableMap<HardwareType, PaoType> hardwareTypeToPaoType = ImmutableMap.<HardwareType, PaoType> builder()
            .put(HardwareType.HONEYWELL_9000, PaoType.HONEYWELL_9000)
            .put(HardwareType.HONEYWELL_FOCUSPRO, PaoType.HONEYWELL_FOCUSPRO)
            .put(HardwareType.HONEYWELL_VISIONPRO_8000, PaoType.HONEYWELL_VISIONPRO_8000)
            .put(HardwareType.HONEYWELL_THERMOSTAT, PaoType.HONEYWELL_THERMOSTAT)
            .build();
    
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    private final Map<Integer, String> inventoryIdToSerialNumber = new HashMap<>();
    
    @Override
    public void createDevice(Hardware hardware) {
        createDevice(hardware.getInventoryId(), hardware.getSerialNumber(), hardware.getHardwareType(),
            hardware.getMacAddress(), hardware.getDeviceVendorUserId());
    }
    
    public void createDevice(int inventoryId, String serialNumber, HardwareType hardwareType, String macAddress,
            Integer deviceVendorUserId) {
        try {
          //TODO: Code to register new device with honeywell service??

            CompleteHoneywellWifiThermostat honeywellPao = new CompleteHoneywellWifiThermostat();
            honeywellPao.setPaoName(serialNumber);
            honeywellPao.setMacAddress(macAddress);
            honeywellPao.setUserId(deviceVendorUserId);
            paoPersistenceService.createPaoWithDefaultPoints(honeywellPao, hardwareTypeToPaoType.get(hardwareType));

            // Update the Stars table with the device id
            inventoryBaseDao.updateInventoryBaseDeviceId(inventoryId, honeywellPao.getPaObjectId());
          //TODO: Code to move device to the enrollmentset using honeywell service??
        } catch (Exception e) {
            //Catch any exception here - only honeywell exceptions (most often communications) are expected, but we might
            //also have authentication exceptions (which cannot be explicitly caught here) or something unexpected.
            log.error("Unable to create device.", e);
            throw new DeviceCreationException(e.getMessage(), "invalidDeviceCreation", e);
        }
    }
    
    @Override
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId) {
        //Get the inventory, while it still exists, and cache the serial number so we can send the honeywell delete request.
        LiteInventoryBase inventory = inventoryBaseDao.getByInventoryId(inventoryId.getInventoryId());
        inventoryIdToSerialNumber.put(inventoryId.getInventoryId(), inventory.getManufacturerSerialNumber());
    }
    
    @Override
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId) {
        paoPersistenceService.deletePao(pao.getPaoIdentifier());
        //Inventory has been deleted, so get the serial number from the cache and send the honeywell delete request.
        String serialNumber = inventoryIdToSerialNumber.remove(inventoryId.getInventoryId());
      //TODO: Code to send deletion msg to the honeywell service
    }

    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return hardwareTypeToPaoType.keySet();
    }

    @Override
    public void updateDevice(Hardware hardware) {
        updateDevice(hardware.getInventoryId(), hardware.getMacAddress(), hardware.getDeviceId(), hardware.getDeviceVendorUserId(),
            hardware.getYukonPao());
    }

    public void updateDevice(int inventoryId, String macAddress, int deviceId, Integer deviceVendorUserId, YukonPao pao) {
        CompleteHoneywellWifiThermostat honeywellThermostat =
            paoPersistenceService.retreivePao(pao, CompleteHoneywellWifiThermostat.class);
        honeywellThermostat.setMacAddress(macAddress);
        honeywellThermostat.setUserId(deviceVendorUserId);
        paoPersistenceService.updatePao(honeywellThermostat);
        inventoryBaseDao.updateInventoryBaseDeviceId(inventoryId, deviceId);
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

        String macAddress = hardware.getMacAddress();
        if (StringUtils.isBlank(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required");
        } else if (!Validator.isMacAddress(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.format.eui48");
        } else if (hardware.getDeviceVendorUserId() == null) {
            errors.rejectValue("deviceVendorUserId", "yukon.web.modules.operator.hardware.error.required");
        }

    }
}
