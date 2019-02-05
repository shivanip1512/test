package com.cannontech.stars.dr.itron;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.cannontech.stars.dr.hardware.exception.DeviceMacAddressAlreadyExistsException;
import com.cannontech.util.Validator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ItronBuilder implements HardwareTypeExtensionProvider {
        
    private static final ImmutableMap<HardwareType, PaoType> hardwareTypeToPaoType = ImmutableMap.<HardwareType, PaoType> builder()
            .put(HardwareType.LCR_6600S, PaoType.LCR6600S)
            .put(HardwareType.LCR_6601S, PaoType.LCR6601S)
            .build();
    
    @Autowired private DeviceCreationService creationService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private ItronCommunicationService itronCommunicationService;
    
    @Transactional
    @Override
    public void createDevice(Hardware hardware) {
        if (deviceDao.isMacAddressExists(hardware.getMacAddress())) {
            throw new DeviceMacAddressAlreadyExistsException();
        }
        SimpleDevice pao = creationService.createDeviceByDeviceType(hardwareTypeToPaoType.get(hardware.getHardwareType()), hardware.getSerialNumber());
        inventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), pao.getDeviceId());
        deviceDao.updateDeviceMacAddress(pao.getDeviceId(), hardware.getMacAddress());
        itronCommunicationService.addDevice(hardware);
    }

    @Override
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId) {
       
    }
    
    @Override
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId) {
        itronCommunicationService.removeDeviceFromServicePoint(pao.getPaoIdentifier().getPaoId());
        deviceDao.removeDevice(pao.getPaoIdentifier().getPaoId());
    }

    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return hardwareTypeToPaoType.keySet();
    }

    @Override
    public void updateDevice(Hardware hardware) {

    }

    public void updateDevice(int inventoryId, String macAddress, int deviceId, Integer deviceVendorUserId, YukonPao pao) {

    }

    @Override
    public void moveDeviceToInventory(YukonPao pao, InventoryIdentifier inventoryId) {
        itronCommunicationService.removeDeviceFromServicePoint(pao.getPaoIdentifier().getPaoId());
    }

    @Override
    public void retrieveDevice(Hardware hardware) {
     
    }

    @Override
    public void validateDevice(Hardware hardware, Errors errors) {
        String macAddress = hardware.getMacAddress();
        if (StringUtils.isBlank(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required");
        } else if (!Validator.isMacAddress(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.format.eui48");
        }
    }
}
