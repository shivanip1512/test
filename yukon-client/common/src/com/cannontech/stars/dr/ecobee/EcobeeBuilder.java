package com.cannontech.stars.dr.ecobee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.CompleteDevice;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.dr.ecobee.EcobeeException;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.google.common.collect.ImmutableSet;

public class EcobeeBuilder implements HardwareTypeExtensionProvider {

    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;

    private static final ImmutableSet<HardwareType> supportedType = ImmutableSet.of(HardwareType.ECOBEE_SMART_SI);

    @Override
    public void createDevice(Hardware hardware) {
        try {
            ecobeeCommunicationService.registerDevice(hardware.getSerialNumber(), hardware.getEnergyCompanyId());

            CompleteDevice ecobeePao = new CompleteDevice();
            ecobeePao.setPaoName(hardware.getSerialNumber());
            paoPersistenceService.createPaoWithDefaultPoints(ecobeePao, PaoType.ECOBEE_SMART_SI);

            // Update the Stars table with the device id
            inventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), ecobeePao.getPaObjectId());
        } catch (EcobeeException e) {
            throw new RuntimeException("Device creation failed", e);
        }
    }

    @Override
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId) {
        paoPersistenceService.deletePao(pao.getPaoIdentifier());
    }

    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return supportedType;
    }

    @Override
    public void updateDevice(Hardware hardware) {
        // Nothing extra to do
    }

    @Override
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId) {
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
}
