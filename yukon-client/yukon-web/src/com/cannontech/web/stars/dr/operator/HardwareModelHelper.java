package com.cannontech.web.stars.dr.operator;

import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.endpoint.IgnoredTemplateException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.exception.DeviceMacAddressAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.web.stars.dr.operator.hardware.validator.HardwareValidator;
import com.cannontech.web.util.SessionUtil;

public class HardwareModelHelper {
    
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private HardwareValidator hardwareValidator;
    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private CustomerAccountDao customerAccountDao;
    
    private final static String deviceCreationErrorKey = "yukon.web.modules.operator.hardware.error.deviceCreationError";

    public void creationAttempted(LiteYukonUser user, 
                                  String accountNo, 
                                  Hardware hardware, 
                                  Set<YukonRoleProperty> verifyProperties, 
                                  BindingResult result) {
        
        hardwareEventLogService.hardwareCreationAttempted(user, accountNo, hardware.getSerialNumber(), EventSource.OPERATOR);
        if (hardware.isCreatingNewTwoWayDevice()) {
            hardwareEventLogService.twoWayHardwareCreationAttempted(user, hardware.getTwoWayDeviceName(), EventSource.OPERATOR);
        }

        for (YukonRoleProperty property : verifyProperties) {
            rolePropertyDao.verifyProperty(property, user);
        }

        hardwareValidator.validate(hardware, result);
    }
    
    public int create(LiteYukonUser user, Hardware hardware, BindingResult result, HttpSession session) {

        int inventoryId = -1;
        try {
            HardwareType type = hardware.getHardwareType();
            if (type.isRf()) {
                /** For rf devices the {@link RfnDeviceCreationService} will end up calling {@link HardwareUiService} createHardware method 
                 * after it creates the pao part of the device using the {@link DeviceCreationService}. */
                PaoType paoType = type.getForHardwareType();
                RfnManufacturerModel templateSettings = RfnManufacturerModel.getForType(paoType).get(0);
                RfnIdentifier rfId = new RfnIdentifier(hardware.getSerialNumber(), templateSettings.getManufacturer(), templateSettings.getModel());
                rfnDeviceCreationService.create(rfId, hardware, user);
                inventoryId = hardware.getInventoryId();
            } else {
                inventoryId = hardwareUiService.createHardware(hardware, user);
            }

            /* If the device status was set, spawn an event for it. */
            /* This is within the try block because it relies on inventoryId being set */
            if (hardware.getDeviceStatusEntryId() != null && hardware.getDeviceStatusEntryId() != 0) {
                int userId = SessionUtil.getParentLoginUserId(session, user.getUserID());
                EventUtils.logSTARSEvent(userId, EventUtils.EVENT_CATEGORY_INVENTORY, hardware.getDeviceStatusEntryId(), inventoryId);
            }
        } catch (IgnoredTemplateException e) {  //includes BadTemplateDeviceCreationException 
            result.rejectValue("serialNumber", deviceCreationErrorKey, new Object[]{e.getMessage()}, "");
        } catch (DeviceCreationException e) {
            switch (e.getType()) {
            case UNKNOWN:
                result.rejectValue("serialNumber", deviceCreationErrorKey, new Object[]{e.getMessage()}, "");
                break;
            case GUID_ALREADY_EXISTS:
            case GUID_DOES_NOT_EXIST:
                result.rejectValue("guid", deviceCreationErrorKey, new Object[]{e.getMessage()}, "");
                break;
            }
        } catch (StarsDeviceSerialNumberAlreadyExistsException|ObjectInOtherEnergyCompanyException e) {
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
        } catch (DeviceMacAddressAlreadyExistsException e) {
            result.rejectValue("macAddress",
                "yukon.web.modules.operator.hardware.error.unavailable.macAddress");
        }
        return inventoryId;
    }
    
    public void updateAttempted(Hardware hardware, LiteYukonUser user, YukonRoleProperty editingRp, BindingResult result) {
        CustomerAccount custAccount = customerAccountDao.getById(hardware.getAccountId());
        hardwareEventLogService.hardwareUpdateAttempted(user, custAccount.getAccountNumber(), hardware.getSerialNumber(), EventSource.OPERATOR);
        rolePropertyDao.verifyProperty(editingRp, user);
        hardwareValidator.validate(hardware, result);
    }
}