package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.dr.hardware.builder.HardwareTypeExtensionService;

public class HardwareValidator extends SimpleValidator<Hardware> {
    
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private PaoDao paoDao;
    @Autowired private HardwareTypeExtensionService hardwareTypeExtensionService;
    
    public HardwareValidator() {
    	super(Hardware.class);
    }

    @Override
    public void doValidation(Hardware hardware, Errors errors) {
        HardwareType hardwareType = hardware.getHardwareType();

        /* Set the type for */
        hardware.setHardwareType(hardwareType);
        
        //This will validate any hardware extensions. Ex. Zigbee devices
        hardwareTypeExtensionService.validateDevice(hardware, errors);
        
        /* Serial Number */
        if (!hardwareType.isMeter()) {  /* Check serial numbers for switches and tstats */
            if (StringUtils.isBlank(hardware.getSerialNumber())) {
                errors.rejectValue("serialNumber", "yukon.web.error.required");
            } else {
                validateSN(hardware.getSerialNumber(), errors, hardwareType, "serialNumber");
            }
        } else if (hardwareType == HardwareType.NON_YUKON_METER) {
            /* Meter Number */
            if (StringUtils.isBlank(hardware.getMeterNumber())) {
                errors.rejectValue("meterNumber", "yukon.web.modules.operator.meterProfile.error.required");
            } else {
                YukonValidationUtils.checkExceedsMaxLength(errors, "meterNumber", hardware.getMeterNumber(), 30);
            }
        }
        
        /* Device Label */
        if (StringUtils.isNotBlank(hardware.getDisplayLabel())) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "displayLabel", hardware.getDisplayLabel(), 60);
        }
        
        /* Alternate Tracking Number */
        if (StringUtils.isNotBlank(hardware.getAltTrackingNumber())) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "altTrackingNumber", hardware.getAltTrackingNumber(), 40);
        }
        
        /* Device Info Notes */
        if (StringUtils.isNotBlank(hardware.getDeviceNotes())) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "deviceNotes", hardware.getDeviceNotes(), 500);
        }
        
        /* Install Notes */
        if (StringUtils.isNotBlank(hardware.getInstallNotes())) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "installNotes", hardware.getInstallNotes(), 500);
        }
        
        /* LCR-3102 */
        if (hardwareType == HardwareType.LCR_3102) {
            
            if (!hardware.isCreatingNewTwoWayDevice()) {
                /* Using the picker to choose a two way device */
                
                /* If they have not picked a device for this two way inventory, reject this device id */
                if (hardware.getDeviceId() <= 0) {
                    errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.invalid");
                } else {
                
                    try {
                        /* If this device no longer exists, reject this device id. */
                        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(hardware.getDeviceId());
                        PaoType paoType = pao.getPaoIdentifier().getPaoType();
                        
                        if(!DeviceTypesFuncs.isTwoWayLcr(paoType.getDeviceTypeId())) {
                            /* The device with this id is no longer a two way lcr. */
                            errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.invalidDeviceType", new Object[] {pao.getLiteID(), pao.getPaoName()}, null);
                        }
                        
                        /* Device can only be used by one lcr at a time */
                        try {
                            LiteInventoryBase inventoryBase = inventoryBaseDao.getByDeviceId(hardware.getDeviceId());
                            if (hardware.getInventoryId() == null) {
                                /* Creating a two way lcr and the two way device is already in use */
                                String unavailableDeviceName = pao.getPaoName();
                                errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.unavailable", new String[] {unavailableDeviceName}, null);
                            } else {
                                /* Updating a two way lcr, see if the inventory for this device is us */
                                LiteInventoryBase inventory = inventoryBaseDao.getByInventoryId(hardware.getInventoryId());
                                /* If something is using this device and it's not this inventory reject this device id */
                                if (inventoryBase.getDeviceID() != inventory.getDeviceID()) {
                                    String unavailableDeviceName = pao.getPaoName();
                                    errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.unavailable", new String[] {unavailableDeviceName}, null);
                                }
                            }
                        } catch (NotFoundException e) {/* Ok */}
                        
                        /* The device's address must match the LCR's serial number */
                        try {
                            int serial = Integer.valueOf(hardware.getSerialNumber());
                            if (pao.getAddress() != serial) {
                                errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.addressMismatch", new String[] {pao.getPaoName()}, null);
                            }
                        } catch (NumberFormatException ignore) {/* Ignore this since we are checking serial number above. */}
                        
                    } catch (NotFoundException e) {
                        /* Device no longer exists.*/
                        errors.rejectValue("deviceId", "yukon.web.modules.operator.hardware.error.notFound");
                    }
                }
            } else {
                /* They are creating a new two way device and should have supplied a valid pao name */
                if (StringUtils.isNotBlank(hardware.getTwoWayDeviceName())) {
                    if (!paoDao.isNameAvailable(hardware.getTwoWayDeviceName(), PaoType.LCR3102)) {
                        errors.rejectValue("twoWayDeviceName", "yukon.web.modules.operator.hardware.error.unavailable");
                    }
                } else {
                    YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "twoWayDeviceName", "yukon.web.modules.operator.hardware.error.required");
                }
            }
        }
    }
    
    private void validateSN(String sn, Errors errors, HardwareType type, String path) {
        if (type == HardwareType.LCR_3102) {
            /* For LCR 3102's the serial number must be a valid integer since it has to match the 
             * address in DeviceCarrierSettings which is a varchar(18) */
            if (!StringUtils.isNumeric(sn)) {
                errors.rejectValue(path, "yukon.web.modules.operator.hardware.error.nonNumericSerialNumber");
            } else {
               try {
                   Integer.parseInt(sn);
               } catch(NumberFormatException e) {
                   errors.rejectValue(path, "yukon.web.modules.operator.hardware.error.tooLong.twoWay");
               }
            }
        } else {
            /* Not a LCR 3102 so serial number should only contain alpha numeric characters and
             * be less than 30 characters long. */
            if (!StringUtils.isAlphanumeric(sn)) {
                errors.rejectValue(path, "yukon.web.modules.operator.hardware.error.invalid.alphanumeric");
            } else {
                YukonValidationUtils.checkExceedsMaxLength(errors, path, sn, 30);
            }
        }
    }
    
}