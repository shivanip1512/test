package com.cannontech.web.stars.dr.operator.hardware.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;

public class HardwareDtoValidator extends SimpleValidator<HardwareDto> {
    
    private static final char[] validSerialNumberChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private InventoryBaseDao inventoryBaseDao;
    private PaoDao paoDao;
    
    public HardwareDtoValidator() {
    	super(HardwareDto.class);
    }

    @Override
    public void doValidation(HardwareDto hardwareDto, Errors errors) {
        
        /* Serial Number */
        if(!hardwareDto.isMct()){  /* Check serial numbers for switches and tstats */
            if (StringUtils.isBlank(hardwareDto.getSerialNumber())) {
                errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.required");
            } else if(hardwareDto.getSerialNumber().length() > 30) {
                errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.tooLong");
            } else if (hardwareDto.isTwoWayLcr() && !StringUtils.containsOnly(hardwareDto.getSerialNumber(), validSerialNumberChars)){
                errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.invalid");
            }
        }
        
        /* Device Label */
        if (StringUtils.isNotBlank(hardwareDto.getDisplayLabel())) {
            if(hardwareDto.getDisplayLabel().length() > 60){
                errors.rejectValue("displayLabel", "yukon.web.modules.operator.hardwareEdit.error.tooLong");
            }
        }
        
        /* Alternate Tracking Number */
        if (StringUtils.isNotBlank(hardwareDto.getAltTrackingNumber())) {
            if(hardwareDto.getAltTrackingNumber().length() > 40){
                errors.rejectValue("altTrackingNumber", "yukon.web.modules.operator.hardwareEdit.error.tooLong");
            }
        }
        
        /* Device Info Notes */
        if (StringUtils.isNotBlank(hardwareDto.getDeviceNotes())) {
            if(hardwareDto.getDeviceNotes().length() > 500){
                errors.rejectValue("deviceNotes", "yukon.web.modules.operator.hardwareEdit.error.tooLong");
            }
        }
        
        /* Install Notes */
        if (StringUtils.isNotBlank(hardwareDto.getInstallNotes())) {
            if(hardwareDto.getInstallNotes().length() > 500){
                errors.rejectValue("installNotes", "yukon.web.modules.operator.hardwareEdit.error.tooLong");
            }
        }
        
        /* Two Way LCR's */
        if(hardwareDto.isTwoWayLcr()){
            /* If they have not picked a device for this two way inventory, reject this device id */
            if(!(hardwareDto.getDeviceId() > 0)){
                errors.rejectValue("deviceId", "yukon.web.modules.operator.hardwareEdit.error.invalid");
            }
            
            try {
                /* If this device no longer exists, reject this device id. */
                LiteYukonPAObject pao = paoDao.getLiteYukonPAO(hardwareDto.getDeviceId());
                PaoType paoType = pao.getPaoIdentifier().getPaoType();
                
                if(!DeviceTypesFuncs.isTwoWayLcr(paoType.getDeviceTypeId())) {
                    /* The device with this id is no longer a two way lcr. */
                    errors.rejectValue("deviceId", "yukon.web.modules.operator.hardwareEdit.error.invalidDeviceType", new Object[] {pao.getLiteID(), pao.getPaoName()}, null);
                }
                
                /* Device can only be used by one lcr at a time */
                List<InventoryBase> matchedInventory = inventoryBaseDao.getByDeviceId(hardwareDto.getDeviceId());
                InventoryBase inventory = inventoryBaseDao.getById(hardwareDto.getInventoryId());
                /* If something is using this device and it's not this inventory reject this device id */
                if (matchedInventory.size() != 0 && matchedInventory.get(0).getDeviceId() != inventory.getDeviceId()) {
                    String unavailableDeviceName = pao.getPaoName();
                    errors.rejectValue("deviceId", "yukon.web.modules.operator.hardwareEdit.error.unavailable", new String[] {unavailableDeviceName}, null);
                }
                
                /* The device's address must match the LCR's serial number */
                try {
                    int serial = Integer.valueOf(hardwareDto.getSerialNumber());
                    if (pao.getAddress() != serial) {
                        errors.rejectValue("deviceId", "yukon.web.modules.operator.hardwareEdit.error.addressMismatch", new String[] {pao.getPaoName()}, null);
                    }
                } catch (NumberFormatException ignore) {/* Ignore this since we are checking serial number above. */}
                
            } catch (NotFoundException e) {
                errors.rejectValue("deviceId", "yukon.web.modules.operator.hardwareEdit.error.notFound");
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
}