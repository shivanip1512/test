package com.cannontech.web.amr.monitor.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class OutageMonitorValidator extends SimpleValidator<OutageMonitor> {

    public OutageMonitorValidator() {
        super(OutageMonitor.class);
    }

    @Autowired private OutageMonitorDao outageMonitorDao;
    @Autowired private DeviceGroupService deviceGroupService;

    @Override
    protected void doValidation(OutageMonitor outageMonitor, Errors errors) {
        validateName(outageMonitor, errors);
        if (!errors.hasErrors() && (outageMonitor.getGroupName() == null
            || deviceGroupService.findGroupName(outageMonitor.getGroupName()) == null)) {
            errors.reject("yukon.web.modules.amr.invalidGroupName");
        }
        YukonValidationUtils.checkIsValidNumber(errors, "numberOfOutages", outageMonitor.getNumberOfOutages());
        if (!errors.hasFieldErrors("numberOfOutages") && outageMonitor.getNumberOfOutages() < 1) {
            errors.rejectValue("numberOfOutages", "yukon.web.modules.amr.outageMonitorConfig.invalidNumberOfOutages");
        }

        YukonValidationUtils.checkIsValidNumber(errors, "timePeriodDays", outageMonitor.getTimePeriodDays());
        if (!errors.hasFieldErrors("timePeriodDays") && outageMonitor.getTimePeriodDays() < 0) {
            errors.rejectValue("timePeriodDays", "yukon.web.modules.amr.outageMonitorConfig.invalidTimePeriod");
        }
        if (outageMonitor.isScheduleGroupCommand() && StringUtils.isBlank(outageMonitor.getScheduleName())) {
            errors.rejectValue("scheduleName", "yukon.web.modules.amr.outageMonitorConfig.scheduleName.blank");
        }
    }

    private void validateName(OutageMonitor outageMonitor, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "outageMonitorName", "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("outageMonitorName")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "outageMonitorName",
                outageMonitor.getOutageMonitorName(), 60);
            if (!DeviceGroupUtil.isValidName(outageMonitor.getOutageMonitorName())) {
                errors.rejectValue("outageMonitorName", "yukon.web.error.deviceGroupName.containsIllegalChars");
            }
        }

        boolean idSpecified = outageMonitor.getOutageMonitorId() != null;

        boolean nameAvailable = !outageMonitorDao.processorExistsWithName(outageMonitor.getOutageMonitorName());

        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("outageMonitorName", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                OutageMonitor existingOutageMonitor = outageMonitorDao.getById(outageMonitor.getOutageMonitorId());
                if (!existingOutageMonitor.getOutageMonitorName().equals(outageMonitor.getOutageMonitorName())) {
                    errors.rejectValue("outageMonitorName", "yukon.web.error.nameConflict");
                }
            }
        }
    }

}
