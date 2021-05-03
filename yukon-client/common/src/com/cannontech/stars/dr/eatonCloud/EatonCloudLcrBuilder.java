package com.cannontech.stars.dr.eatonCloud;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.pxmw.model.PxMWException;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.dr.pxmw.service.v1.PxMWDataReadService;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.cannontech.util.Validator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class EatonCloudLcrBuilder implements HardwareTypeExtensionProvider {
        
    private static final Logger log = YukonLogManager.getLogger(EatonCloudLcrBuilder.class);
    private static final ImmutableMap<HardwareType, PaoType> hardwareTypeToPaoType = ImmutableMap.<HardwareType, PaoType> builder()
            .put(HardwareType.LCR_6200C, PaoType.LCR6200C)
            .put(HardwareType.LCR_6600C, PaoType.LCR6600C)
            .put(HardwareType.LCR_DISCONNECT_C, PaoType.LCR_DISCONNECT_C)
            .build();
    
    @Autowired private DeviceCreationService creationService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PxMWDataReadService readService;
    @Autowired PxMWCommunicationServiceV1 pxMWCommunicationServiceV1;
    
    @Override
    public void createDevice(Hardware hardware) {
        try {
            if (deviceDao.isGuidExists(hardware.getGuid())) {
                throw new PxMWException("Guid:" + hardware.getGuid() + " already exits for " + hardware.getDeviceId());
            }     
            SimpleDevice pao = creationService.createDeviceByDeviceType(
                hardwareTypeToPaoType.get(hardware.getHardwareType()), hardware.getSerialNumber());
            inventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), pao.getDeviceId());
            deviceDao.insertGuid(pao.getDeviceId(), hardware.getGuid());
            DateTime start = new DateTime();
            DateTime end = start.minusDays(1);
            Range<Instant> range =  new Range<Instant>(end.toInstant(), false, start.toInstant(), true);
            readService.collectDataForRead(Set.of(pao.getDeviceId()), range);
        } catch (PxMWCommunicationExceptionV1 | PxMWException e) {
            log.error("Unable to create device.", e);
            throw new DeviceCreationException(e.getMessage(), "invalidDeviceCreation", e);
        }
    }

    @Override
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId) {
       // Nothing to do.
    }
    
    @Override
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId) {
        deviceDao.removeDevice(pao.getPaoIdentifier().getPaoId());
    }

    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return hardwareTypeToPaoType.keySet();
    }

    @Override
    public void updateDevice(Hardware hardware) {
        deviceDao.updateGuid(hardware.getDeviceId(), hardware.getGuid());
    }

    @Override
    public void moveDeviceToInventory(YukonPao pao, InventoryIdentifier inventoryId) {
    }

    @Override
    public void retrieveDevice(Hardware hardware) {
        try {
            hardware.setGuid(deviceDao.getGuid(hardware.getDeviceId()));
        } catch (NotFoundException nfe) {
            log.error("GUID is not found device id:" + hardware.getDeviceId(), nfe);
            hardware.setGuid("");
        }
    }

    @Override
    public void validateDevice(Hardware hardware, Errors errors) {
        String guid = hardware.getGuid();
        if (StringUtils.isBlank(guid)) {
            errors.rejectValue("guid", "yukon.web.error.required");
        } else if (!Validator.isValidGuid(guid)) {
            errors.rejectValue("guid", "yukon.web.modules.operator.hardware.error.format.guid");
        }
    }
}
