package com.cannontech.stars.dr.eatonCloud;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException.Type;
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
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoDao.InfoKey;
import com.cannontech.dr.eatonCloud.model.EatonCloudException;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudDeviceDetailV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudDataReadService;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.cannontech.util.Validator;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;


public class EatonCloudLcrBuilder implements HardwareTypeExtensionProvider {
        
    private static final Logger log = YukonLogManager.getLogger(EatonCloudLcrBuilder.class);
    private static final ImmutableMap<HardwareType, PaoType> hardwareTypeToPaoType = ImmutableMap.<HardwareType, PaoType> builder()
            .put(HardwareType.LCR_6200C, PaoType.LCR6200C)
            .put(HardwareType.LCR_6600C, PaoType.LCR6600C)
            .build();
    
    @Autowired private DeviceCreationService creationService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private EatonCloudDataReadService readService;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationServiceV1;
    @Autowired private PaoDao paoDao;
    
    @Override
    public void createDevice(Hardware hardware) {
        try {
            if (deviceDao.isGuidExists(hardware.getGuid())) {
                throw new DeviceCreationException("Guid:" + hardware.getGuid() + " already exists.", "invalidDeviceCreation", Type.GUID_ALREADY_EXISTS);
            }     
                        
            try {
                EatonCloudDeviceDetailV1 detail = eatonCloudCommunicationServiceV1.getDeviceDetails(hardware.getGuid(), false);
                if (!isSimulator(detail) && !isValidSerialNumber(detail, hardware)) {
                    throw new DeviceCreationException(
                            "Invalid serial number:" + hardware.getSerialNumber()
                                    + ". Your Brightlayer site has a serial number:" + detail.getSerial() + " For GUID:"
                                    + hardware.getGuid() + ". Device cannot be added to Yukon at this time.",
                            "invalidDeviceCreation", Type.UNKNOWN);
                }
            } catch (EatonCloudCommunicationExceptionV1 e) {
                if (e.getErrorMessage() != null && e.getErrorMessage().getErrorCode() == HttpStatus.BAD_REQUEST.value()) {
                    throw new DeviceCreationException("Unable to find a matching device identifier GUID:" + hardware.getGuid()
                            + " registered in your Brightlayer site. Device cannot be added to Yukon at this time.",
                            "invalidDeviceCreation", Type.GUID_DOES_NOT_EXIST, e);
                }
                throw e;
            }

            SimpleDevice pao = creationService.createDeviceByDeviceType(
                hardwareTypeToPaoType.get(hardware.getHardwareType()), hardware.getSerialNumber());
            inventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), pao.getDeviceId());
            deviceDao.insertGuid(pao.getDeviceId(), hardware.getGuid());
            DateTime start = new DateTime();
            DateTime end = start.minusDays(1);
            Range<Instant> range =  new Range<Instant>(end.toInstant(), false, start.toInstant(), true);
            try {
                readService.collectDataForRead(pao.getDeviceId(), range);
            } catch (EatonCloudCommunicationExceptionV1 | EatonCloudException e) {
                log.error("Unable to read device:{}", pao, e);
            }
        } catch (EatonCloudCommunicationExceptionV1 | EatonCloudException e) {
            log.error("Unable to create device.", e);
            throw new DeviceCreationException(e.getMessage(), "invalidDeviceCreation", e);
        }
    }
    
    /**
     * To test invalid serial number error, make this method return false. Returns true if responses are simulated.
     */
    private boolean isSimulator(EatonCloudDeviceDetailV1 detail) {
        return !Strings.isNullOrEmpty(detail.getSoftware()) && detail.getSoftware().equals("YUKON_SIMULATOR");
    }
    
    /**
     * Returns true if the serial number matches serial number for the GUID in Brightlayer.
     */
    private boolean isValidSerialNumber(EatonCloudDeviceDetailV1 detail, Hardware hardware) {
        return !Strings.isNullOrEmpty(detail.getSerial()) && hardware.getSerialNumber().equals(detail.getSerial());
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
            hardware.setFirmwareVersion(paoDao.findPaoInfoValue(hardware.getDeviceId(), InfoKey.FIRMWARE_VERSION));
        } catch (NotFoundException nfe) {
            log.debug("GUID is not found device id:" + hardware.getDeviceId());
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
