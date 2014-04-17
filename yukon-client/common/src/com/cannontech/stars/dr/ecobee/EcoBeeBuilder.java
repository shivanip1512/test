package com.cannontech.stars.dr.ecobee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.CompleteDevice;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.google.common.collect.ImmutableSet;

public class EcoBeeBuilder implements HardwareTypeExtensionProvider {

    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private InventoryBaseDao inventoryBaseDao;

    @Override
    public void createDevice(Hardware hardware) {
        CompleteDevice ecoBeePao = new CompleteDevice();
        ecoBeePao.setPaoName(hardware.getSerialNumber());
        paoPersistenceService.createPaoWithDefaultPoints(ecoBeePao, PaoType.ECOBEE_SMART_SI);

        // Update the Stars table with the device id
        inventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), ecoBeePao.getPaObjectId());
    }

    @Override
    public void updateDevice(Hardware hardware) {
//        PaoIdentifier ecoBeeIdentifier = new PaoIdentifier(hardware.getDeviceId(), PaoType.ECOBEE_SMART_SI);
//        CompleteDevice ecoBeePao = paoPersistenceService.retreivePao(ecoBeeIdentifier, CompleteDevice.class);
//
//        paoPersistenceService.updatePao(ecoBeePao);
//
//        inventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), hardware.getDeviceId());
    }

    @Override
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId) {
        // Nothing extra to do
    }

    @Override
    @Transactional
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId) {
        paoPersistenceService.deletePao(pao.getPaoIdentifier());
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

    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return ImmutableSet.of(HardwareType.ECOBEE_SMART_SI);
    }
}
