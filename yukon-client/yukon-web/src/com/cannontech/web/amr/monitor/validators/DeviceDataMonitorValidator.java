package com.cannontech.web.amr.monitor.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class DeviceDataMonitorValidator extends SimpleValidator<DeviceDataMonitor> {
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;

    public DeviceDataMonitorValidator() {
        super(DeviceDataMonitor.class);
    }

    @Override
    public void doValidation(DeviceDataMonitor monitor, Errors errors) {
        validateName(monitor, errors);
        if (!errors.hasErrors()
            && (monitor.getGroupName() == null || deviceGroupService.findGroupName(monitor.getGroupName()) == null)) {
            errors.reject("yukon.web.modules.amr.invalidGroupName");
        }
    }

    private void validateName(DeviceDataMonitor monitor, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 60);
            // Check the monitor name does not contain any invalid characters as we use it in Violations Group
            if (!DeviceGroupUtil.isValidName(monitor.getName())) {
                errors.rejectValue("name", "yukon.web.error.deviceGroupName.containsIllegalChars");
            }
        }

        boolean idSpecified = monitor.getId() != null;

        boolean nameAvailable = !deviceDataMonitorDao.processorExistsWithName(monitor.getName());

        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                DeviceDataMonitor existingDeviceDataMonitor = deviceDataMonitorDao.getMonitorById(monitor.getId());
                if (!existingDeviceDataMonitor.getName().equals(monitor.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
