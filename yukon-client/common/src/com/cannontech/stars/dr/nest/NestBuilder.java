package com.cannontech.stars.dr.nest;

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
import com.cannontech.common.pao.model.CompleteDevice;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class NestBuilder implements HardwareTypeExtensionProvider {
    private static final Logger log = YukonLogManager.getLogger(NestBuilder.class);

    private static final ImmutableMap<HardwareType, PaoType> hardwareTypeToPaoType =
        ImmutableMap.<HardwareType, PaoType> builder().put(HardwareType.NEST_THERMOSTAT, PaoType.NEST).build();

    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private InventoryBaseDao inventoryBaseDao;

    @Override
    public void createDevice(Hardware hardware) {
        createDevice(hardware.getInventoryId(), hardware.getSerialNumber(), hardware.getHardwareType());
    }

    public void createDevice(int inventoryId, String serialNumber, HardwareType hardwareType) {

        try {
            CompleteDevice nestPao = new CompleteDevice();
            nestPao.setPaoName(serialNumber);

            paoPersistenceService.createPaoWithDefaultPoints(nestPao, hardwareTypeToPaoType.get(hardwareType));

            // Update the Stars table with the device id
            inventoryBaseDao.updateInventoryBaseDeviceId(inventoryId, nestPao.getPaObjectId());

        } catch (Exception e) {
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
        paoPersistenceService.deletePao(pao.getPaoIdentifier());
    }

    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return hardwareTypeToPaoType.keySet();
    }

    @Override
    public void updateDevice(Hardware hardware) {
        // Nothing extra to do
    }

    public void updateDevice(int inventoryId, String serialNumber, int deviceId, YukonPao pao) {

        CompleteDevice nestThermostat = paoPersistenceService.retreivePao(pao, CompleteDevice.class);

        paoPersistenceService.updatePao(nestThermostat);
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
        // Nothing extra to do
    }
}
