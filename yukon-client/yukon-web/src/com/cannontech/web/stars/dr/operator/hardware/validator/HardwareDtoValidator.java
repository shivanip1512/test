package com.cannontech.web.stars.dr.operator.hardware.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.hardware.model.InventoryBase;

public class HardwareDtoValidator extends SimpleValidator<HardwareDto> {
    
    private InventoryBaseDao inventoryBaseDao;
    private PaoDao paoDao;
    private YukonListDao yukonListDao;
    
    public HardwareDtoValidator() {
    	super(HardwareDto.class);
    }

    @Override
    public void doValidation(HardwareDto hardwareDto, Errors errors) {
        HardwareType hardwareType = null;
        if(hardwareDto.getHardwareType() == null) {
            YukonListEntry hardwareTypeEntry = yukonListDao.getYukonListEntry(hardwareDto.getHardwareTypeEntryId());
            hardwareType = HardwareType.valueOf(hardwareTypeEntry.getYukonDefID());
        } else {
            hardwareType = hardwareDto.getHardwareType();
        }
        
        // Zigbee Device Specific
        if (hardwareType.isZigbee()) {
            if (hardwareType.isMeter()) {
                /* Install Code */
                if (StringUtils.isBlank(hardwareDto.getInstallCode())) {
                    errors.rejectValue("installCode", "yukon.web.modules.operator.hardware.error.required");
                }
            } else if (hardwareType.isGateway()) {
                /* Mac Address */
                if (StringUtils.isBlank(hardwareDto.getMacAddress())) {
                    errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required");
                }
                
                /* Firmware Version */
                if (StringUtils.isBlank(hardwareDto.getFirmwareVersion())) {
                    errors.rejectValue("firmwareVersion", "yukon.web.modules.operator.hardware.error.required");
                }
            }
        }
        
        /* Serial Number */
        if(!hardwareType.isMeter()){  /* Check serial numbers for switches and tstats */
            if (StringUtils.isBlank(hardwareDto.getSerialNumber())) {
                errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.required");
            } else {
                if (hardwareType.isSwitch() && hardwareType.isTwoWay()) {
                    /* This is a two way lcr so the serial number can only have numeric chars and must be a valid integer. */
                    if(!StringUtils.isNumeric(hardwareDto.getSerialNumber())){
                        errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.invalid.numeric");
                    } else {
                       try {
                           Integer.parseInt(hardwareDto.getSerialNumber());
                       } catch(NumberFormatException e) {
                           errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.tooLong.twoWay");
                       }
                    }
                } else {
                    /* Not a two way device so serial number should only contain alpha numeric characters and
                     * be less than 30 characters long. */
                    if (!StringUtils.isAlphanumeric(hardwareDto.getSerialNumber())) {
                        errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.invalid.alphanumeric");
                    } else {
                        YukonValidationUtils.checkExceedsMaxLength(errors, "serialNumber", hardwareDto.getSerialNumber(), 30);
                    } 
                }
            }
        } else if (hardwareType == HardwareType.NON_YUKON_METER) {
            /* Meter Number */
            if (StringUtils.isBlank(hardwareDto.getMeterNumber())) {
                errors.rejectValue("meterNumber", "yukon.web.modules.operator.meterProfile.error.required");
            } else {
                YukonValidationUtils.checkExceedsMaxLength(errors, "meterNumber", hardwareDto.getMeterNumber(), 30);
            }
        }
        
        /* Device Label */
        if (StringUtils.isNotBlank(hardwareDto.getDisplayLabel())) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "displayLabel", hardwareDto.getDisplayLabel(), 60);
        }
        
        /* Alternate Tracking Number */
        if (StringUtils.isNotBlank(hardwareDto.getAltTrackingNumber())) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "altTrackingNumber", hardwareDto.getAltTrackingNumber(), 40);
        }
        
        /* Device Info Notes */
        if (StringUtils.isNotBlank(hardwareDto.getDeviceNotes())) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "deviceNotes", hardwareDto.getDeviceNotes(), 500);
        }
        
        /* Install Notes */
        if (StringUtils.isNotBlank(hardwareDto.getInstallNotes())) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "installNotes", hardwareDto.getInstallNotes(), 500);
        }
        
        /* Two Way LCR's */
        if(hardwareDto.getInventoryId() != null && hardwareType.isSwitch() && hardwareType.isTwoWay()){
            /* If they have not picked a device for this two way inventory, reject this device id */
            if(!(hardwareDto.getDeviceId() > 0)){
                errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.invalid");
            } else {
            
                try {
                    /* If this device no longer exists, reject this device id. */
                    LiteYukonPAObject pao = paoDao.getLiteYukonPAO(hardwareDto.getDeviceId());
                    PaoType paoType = pao.getPaoIdentifier().getPaoType();
                    
                    if(!DeviceTypesFuncs.isTwoWayLcr(paoType.getDeviceTypeId())) {
                        /* The device with this id is no longer a two way lcr. */
                        errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.invalidDeviceType", new Object[] {pao.getLiteID(), pao.getPaoName()}, null);
                    }
                    
                    /* Device can only be used by one lcr at a time */
                    List<InventoryBase> matchedInventory = inventoryBaseDao.getByDeviceId(hardwareDto.getDeviceId());
                    InventoryBase inventory = inventoryBaseDao.getById(hardwareDto.getInventoryId());
                    /* If something is using this device and it's not this inventory reject this device id */
                    if (matchedInventory.size() != 0 && matchedInventory.get(0).getDeviceId() != inventory.getDeviceId()) {
                        String unavailableDeviceName = pao.getPaoName();
                        errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.unavailable", new String[] {unavailableDeviceName}, null);
                    }
                    
                    /* The device's address must match the LCR's serial number */
                    try {
                        int serial = Integer.valueOf(hardwareDto.getSerialNumber());
                        if (pao.getAddress() != serial) {
                            errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.addressMismatch", new String[] {pao.getPaoName()}, null);
                        }
                    } catch (NumberFormatException ignore) {/* Ignore this since we are checking serial number above. */}
                    
                } catch (NotFoundException e) {
                    /* Device no longer exists.*/
                    errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.notFound");
                }
            }
        }
    }
    
    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
}