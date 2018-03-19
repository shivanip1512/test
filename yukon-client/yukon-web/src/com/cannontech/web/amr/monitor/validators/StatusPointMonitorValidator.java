package com.cannontech.web.amr.monitor.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class StatusPointMonitorValidator extends SimpleValidator<StatusPointMonitor> {

    @Autowired private StatusPointMonitorDao statusPointMonitorDao;
    @Autowired private DeviceGroupService deviceGroupService;

    public StatusPointMonitorValidator() {
        super(StatusPointMonitor.class);
    }

    @Override
    protected void doValidation(StatusPointMonitor monitor, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "statusPointMonitorName", "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("statusPointMonitorName")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "statusPointMonitorName",
                monitor.getStatusPointMonitorName(), 60);
            // Check the monitor name does not contain any invalid characters as we use it in Violations Group
            if (!DeviceGroupUtil.isValidName(monitor.getStatusPointMonitorName())) {
                errors.rejectValue("statusPointMonitorName", "yukon.web.error.deviceGroupName.containsIllegalChars");
            }
        }

        boolean nameAvailable = !statusPointMonitorDao.monitorExistsWithName(monitor.getStatusPointMonitorName());
        boolean idSpecified = monitor.getStatusPointMonitorId() != null;

        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("statusPointMonitorName", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                StatusPointMonitor existingDeviceDataMonitor =
                    statusPointMonitorDao.getStatusPointMonitorById(monitor.getStatusPointMonitorId());
                if (!existingDeviceDataMonitor.getStatusPointMonitorName().equals(
                    monitor.getStatusPointMonitorName())) {
                    errors.rejectValue("statusPointMonitorName", "yukon.web.error.nameConflict");
                }
            }
        }

        if (idSpecified) {
            if (deviceGroupService.findGroupName(monitor.getGroupName()) == null) {
                errors.rejectValue("groupName", "yukon.web.modules.amr.invalidGroupName");
            }
        }
    }

}
