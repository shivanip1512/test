package com.cannontech.web.stars.dr.operator;

import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.model.Hardware;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.web.stars.dr.operator.hardware.validator.HardwareValidator;

public class HardwareModelHelper {
    
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private HardwareValidator hardwareValidator;
    
    public void creationAttempted(LiteYukonUser user, 
                                  String accountNo, 
                                  Hardware hardware, 
                                  Set<YukonRoleProperty> verifyProperties, 
                                  BindingResult result) {
        
        hardwareEventLogService.hardwareCreationAttemptedByOperator(user, accountNo, hardware.getSerialNumber());
        if (hardware.isCreatingNewTwoWayDevice()) {
            hardwareEventLogService.twoWayHardwareCreationAttemptedByOperator(user, hardware.getTwoWayDeviceName());
        }

        for (YukonRoleProperty property : verifyProperties) {
            rolePropertyDao.verifyProperty(property, user);
        }

        hardwareValidator.validate(hardware, result);
    }
    
    public int create(LiteYukonUser user, Integer accountId, Hardware hardware, BindingResult result, HttpSession session) {

        int inventoryId = -1;
        try {
            inventoryId = hardwareUiService.createHardware(hardware, accountId, user);

            /* If the device status was set, spawn an event for it. */
            /* This is within the try block because it relies on inventoryId being set */
            if (hardware.getDeviceStatusEntryId() != null && hardware.getDeviceStatusEntryId() != 0) {
                EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, hardware.getDeviceStatusEntryId(), inventoryId, session);
            }
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            result.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        } catch (ObjectInOtherEnergyCompanyException e) {
            result.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
        } catch (Lcr3102YukonDeviceCreationException e) {
            switch (e.getType()) {
            case UNKNOWN:
                result.rejectValue("twoWayDeviceName", "yukon.web.modules.operator.hardware.error.unknown");
                break;
            case REQUIRED:
                result.rejectValue("twoWayDeviceName", "yukon.web.modules.operator.hardware.error.required");
                break;
            case UNAVAILABLE:
                result.rejectValue("twoWayDeviceName", "yukon.web.modules.operator.hardware.error.unavailable");
                break;
            case NON_NUMERIC:
                result.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.nonNumericSerialNumber");
                break;
            }
        }
        return inventoryId;
    }
    
    public void updateAttempted(Hardware hardware, LiteYukonUser user, YukonRoleProperty editingRp, BindingResult result) {
        hardwareEventLogService.hardwareUpdateAttemptedByOperator(user, hardware.getSerialNumber());
        rolePropertyDao.verifyProperty(editingRp, user);
        hardwareValidator.validate(hardware, result);
    }
}