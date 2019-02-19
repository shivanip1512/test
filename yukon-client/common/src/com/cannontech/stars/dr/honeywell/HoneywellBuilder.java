package com.cannontech.stars.dr.honeywell;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
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
import com.cannontech.dr.honeywell.service.HoneywellCommunicationService;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.cannontech.stars.dr.hardware.dao.HoneywellWifiThermostatDao;
import com.cannontech.stars.dr.hardware.exception.DeviceMacAddressAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.DeviceMacAddressNotUpdatableException;
import com.cannontech.stars.dr.hardware.exception.DeviceVendorUserIdNotUpdatableException;
import com.cannontech.util.Validator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class HoneywellBuilder implements HardwareTypeExtensionProvider {
    private static final Logger log = YukonLogManager.getLogger(HoneywellBuilder.class);
        
    private static final ImmutableMap<HardwareType, PaoType> hardwareTypeToPaoType = ImmutableMap.<HardwareType, PaoType> builder()
            .put(HardwareType.HONEYWELL_9000, PaoType.HONEYWELL_9000)
            .put(HardwareType.HONEYWELL_FOCUSPRO, PaoType.HONEYWELL_FOCUSPRO)
            .put(HardwareType.HONEYWELL_VISIONPRO_8000, PaoType.HONEYWELL_VISIONPRO_8000)
            .put(HardwareType.HONEYWELL_THERMOSTAT, PaoType.HONEYWELL_THERMOSTAT)
            .build();
    
    @Autowired private HoneywellCommunicationService honeywellCommunicationService;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private HoneywellWifiThermostatDao honeywellWifiThermostatDao;

    @Override
    public void createDevice(Hardware hardware) {
        createDevice(hardware.getInventoryId(), hardware.getSerialNumber(), hardware.getHardwareType(),
            hardware.getMacAddress(), hardware.getDeviceVendorUserId());
    }
    
    public void createDevice(int inventoryId, String serialNumber, HardwareType hardwareType, String macAddress,
            Integer deviceVendorUserId) {

        boolean isMacAddressUnique = honeywellWifiThermostatDao.isHoneywellMacAddressUnique(macAddress);
        if (!isMacAddressUnique) {
            throw new DeviceMacAddressAlreadyExistsException();
        }

        try {
            honeywellCommunicationService.registerDevice(macAddress, deviceVendorUserId);

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
        // Nothing extra to do
    }
    
    @Override
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId) {
        // Get the Honeywell details before they're deleted
        var honeywellPao = honeywellWifiThermostatDao.getHoneywellWifiThermostat(pao.getPaoIdentifier().getPaoId());

        paoPersistenceService.deletePao(pao.getPaoIdentifier());

        honeywellCommunicationService.deleteDevice(honeywellPao.getMacAddress(), honeywellPao.getDeviceVendorUserId());
    }

    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return hardwareTypeToPaoType.keySet();
    }

    @Override
    public void updateDevice(Hardware hardware) {
        // Nothing extra to do
    }

    public void updateDevice(int inventoryId, String macAddress, int deviceId, Integer deviceVendorUserId, YukonPao pao) {

        String existingMacAddress = honeywellWifiThermostatDao.getHoneywellWifiThermostat(deviceId).getMacAddress();

        if (!existingMacAddress.equalsIgnoreCase(macAddress)) {
            throw new DeviceMacAddressNotUpdatableException();
        }

        Integer existingDeviceVendorUserId =
            honeywellWifiThermostatDao.getHoneywellWifiThermostat(deviceId).getDeviceVendorUserId();
        
        if (existingDeviceVendorUserId.intValue() != deviceVendorUserId.intValue()) {
            throw new DeviceVendorUserIdNotUpdatableException();
        }
        
        honeywellCommunicationService.registerDevice(macAddress, deviceVendorUserId);
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
